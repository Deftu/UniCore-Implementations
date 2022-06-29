package xyz.unifycraft.unicore.mixins.client;

import de.jcm.discordgamesdk.activity.Activity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.unifycraft.unicore.UniCoreDiImpl;
import xyz.unifycraft.unicore.UniCoreImpl;
import xyz.unifycraft.unicore.api.UniCore;
import xyz.unifycraft.unicore.api.UniCoreDi;
import xyz.unifycraft.unicore.api.events.*;
import xyz.unifycraft.unicore.api.events.input.KeyboardInputEvent;
import xyz.unifycraft.unicore.event.MouseInputHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//#if MC<=11202
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import org.lwjgl.input.Keyboard;

@Mixin({Minecraft.class})
public abstract class MinecraftMixin {
    @Shadow @Final public File mcDataDir;
    @Shadow public GameSettings gameSettings;
    @Shadow public WorldClient theWorld;
    @Shadow private ServerData currentServerData;
    @Shadow private int serverPort;
    @Unique private List<Integer> twitchKeyCodes;

    @Inject(method = "startGame", at = @At("HEAD"))
    private void onGamePreStarted(CallbackInfo ci) {
        UniCoreDi.initialize(new UniCoreDiImpl());
        if (!UniCore.initialize()) {
            throw new IllegalStateException(UniCore.getName() + " was already initialized. How?");
        }
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V", remap = false))
    private void onGameStarted(CallbackInfo ci) {
        UniCore.getEventBus().post(new InitializationEvent(mcDataDir));
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;updateCameraAndRender(FJ)V", shift = At.Shift.AFTER))
    private void onRenderTick(CallbackInfo ci) {
        UniCore.getEventBus().post(new RenderTickEvent());
    }

    @Inject(method = "runTick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;fireKeyInput()V", remap = false, shift = At.Shift.AFTER))
    private void onKeyInput(CallbackInfo ci) {
        boolean repeatEvents = Keyboard.areRepeatEventsEnabled();
        Keyboard.enableRepeatEvents(true);
        CancellableEvent event = new KeyboardInputEvent(Keyboard.getEventKeyState(), Keyboard.isRepeatEvent(), Keyboard.getEventCharacter(), Keyboard.getEventKey());
        UniCore.getEventBus().post(event);
        Keyboard.enableRepeatEvents(repeatEvents);
        if (event.getCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "runTick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;fireMouseInput()V", remap = false, shift = At.Shift.BEFORE))
    private void onMouseInput(CallbackInfo ci) {
        if (MouseInputHandler.INSTANCE.handle()) {
            ci.cancel();
        }
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    private void onGuiScreenDisplayed(GuiScreen screen, CallbackInfo ci) {
        if (screen instanceof GuiMainMenu || (screen == null && theWorld == null)) {
            Activity activity = new Activity();
            activity.setDetails("In the main menu");
            UniCoreImpl.getInstance().discordCore().updateActivity(activity);
        }

        if (screen == null && theWorld != null) {
            Activity activity = new Activity();
            if (currentServerData != null) {
                StringBuilder builder = new StringBuilder();
                builder.append("Playing on ");
                builder.append(currentServerData.serverIP);
                if (serverPort != 25565 && serverPort != 0) {
                    builder.append(":");
                    builder.append(serverPort);
                }
                activity.setDetails(builder.toString());
            } else activity.setDetails("Playing singleplayer");
            UniCoreImpl.getInstance().discordCore().updateActivity(activity);
        }
    }

    //#if MC<=10809
    // Stop Minecraft from even trying to use Twitch
    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startTimerHackThread()V"))
    private void removeTwitchKeyBindings(CallbackInfo ci) {
        List<KeyBinding> keyBindings = Arrays.asList(gameSettings.keyBindings);
        twitchKeyCodes = keyBindings.stream().filter(keyBinding -> keyBinding.getKeyCategory().equals("key.categories.stream")).map(KeyBinding::getKeyCode).collect(Collectors.toList());
        gameSettings.keyBindings = keyBindings.stream().filter(keyBinding -> !keyBinding.getKeyCategory().equals("key.categories.stream")).toArray(KeyBinding[]::new);
        KeyBinding.getKeybinds().remove("key.categories.stream");
    }

    @Inject(method = "dispatchKeypresses", at = @At("HEAD"), cancellable = true)
    private void cancelTwitchKeyBindings(CallbackInfo ci) {
        if (!Keyboard.getEventKeyState()) return;
        int code = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
        if (twitchKeyCodes.contains(code)) ci.cancel();
    }

    @Inject(method = "initStream", cancellable = true, at = @At("HEAD"))
    private void stopStream(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
    private void redirectStream1(IStream stream) {
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
    private void redirectStream2(IStream stream) {
    }

    @Redirect(method = "shutdownMinecraftApplet", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;shutdownStream()V"))
    private void redirectStream3(IStream stream) {
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderStreamIndicator(F)V"))
    private void redirectStream4() {
    }
    //#endif
}
//#elseif MC>=11202
//$$ import net.minecraft.client.Minecraft;
//$$
//$$ @Mixin({MinecraftClient.class})
//$$ public class MinecraftMixin {
//$$
//$$ }
//#endif