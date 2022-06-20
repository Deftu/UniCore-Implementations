package xyz.unifycraft.unicore.mixins.gui;

//#if MC<=10809
import net.minecraft.client.gui.GuiStreamIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GuiStreamIndicator.class})
public class GuiStreamIndicatorMixin {

    @Inject(method = { "render(II)V", "render(IIII)V", "updateStreamAlpha" }, at = @At("HEAD"), cancellable = true)
    private void cancelRender(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "func_152440_b", at = @At("HEAD"), cancellable = true)
    private void cancelFunc_152440_b(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(16);
    }

    @Inject(method = "func_152438_c", at = @At("HEAD"), cancellable = true)
    private void cancelFunc_152438_c(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(32);
    }

}
//#endif
