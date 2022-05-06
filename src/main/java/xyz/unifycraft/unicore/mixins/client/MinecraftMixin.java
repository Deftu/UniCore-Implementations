package xyz.unifycraft.unicore.mixins.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

@Mixin({Minecraft.class})
public class MinecraftMixin {
    @Shadow @Final public File mcDataDir;
    @Inject(method = "startGame", at = @At("HEAD"))
    private void onGamePreStarted(CallbackInfo ci) {
        UniCoreDi.initialize(new UniCoreDiImpl());
        if (!UniCore.initialize())
            throw new IllegalStateException(UniCore.getName() + " was already initialized. How?");
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V", remap = false))
    private void onGameStarted(CallbackInfo ci) {
        UniCore.getEventBus().post(new InitializationEvent(mcDataDir));
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onRenderTickStart(F)V", remap = false, shift = At.Shift.BEFORE))
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
        if (event.getCancelled())
            ci.cancel();
    }

    @Inject(method = "runTick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;fireMouseInput()V", remap = false, shift = At.Shift.BEFORE))
    private void onMouseInput(CallbackInfo ci) {
        boolean isScrolled = Mouse.getEventDWheel() != 0;
        if (isScrolled) UniCore.getEventBus().post(new MouseScrollEvent(Mouse.getEventDWheel()));
        else {
            boolean isButton = Mouse.getEventButton() != 0;
            if (isButton) {
                CancellableEvent event = new MouseButtonEvent(Mouse.getEventButton(), Mouse.getEventButtonState(), Mouse.getEventX(), Mouse.getEventY());
                UniCore.getEventBus().post(event);
                if (event.getCancelled())
                    ci.cancel();
            } else UniCore.getEventBus().post(new MouseMoveEvent(Mouse.getEventX(), Mouse.getEventY()));
        }
    }
}
//#elseif MC>=11202
//$$ import net.minecraft.client.Minecraft;
//$$
//$$ @Mixin({MinecraftClient.class})
//$$ public class MinecraftMixin {
//$$
//$$ }
//#endif