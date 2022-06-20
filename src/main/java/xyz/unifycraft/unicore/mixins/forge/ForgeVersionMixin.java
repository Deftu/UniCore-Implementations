package xyz.unifycraft.unicore.mixins.forge;

//#if MC<=10809
import net.minecraftforge.common.ForgeVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ForgeVersion.class}, remap = false)
public class ForgeVersionMixin {

    @Inject(method = "startVersionCheck", at = @At("HEAD"), cancellable = true)
    private static void cancelVersionCheck(CallbackInfo ci) {
        ci.cancel();
    }

}
//#endif
