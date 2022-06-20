package xyz.unifycraft.unicore.mixins.client;

import net.minecraft.client.stream.IStream;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.unifycraft.unicore.UniCoreDiImpl;
import xyz.unifycraft.unicore.api.UniCore;
import xyz.unifycraft.unicore.api.UniCoreDi;
import xyz.unifycraft.unicore.api.events.*;

import java.io.File;

//#if MC<=11202
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import xyz.unifycraft.unicore.api.events.input.KeyboardInputEvent;
import xyz.unifycraft.unicore.event.MouseInputHandler;

@Mixin({Minecraft.class})
public class MinecraftMixin {
    @Shadow @Final public File mcDataDir;
    @Shadow public int displayWidth;
    @Shadow public int displayHeight;

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

    @Inject(method = "runTick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;fireMouseInput()V", remap = false, shift = At.Shift.AFTER))
    private void onMouseInput(CallbackInfo ci) {
        if (MouseInputHandler.INSTANCE.handle()) {
            ci.cancel();
        }
    }

    // Stop Minecraft from even trying to use Twitch
    //#if MC<=10809
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