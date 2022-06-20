package xyz.unifycraft.unicore.mixins.forge;

//#if MC<=11202
import net.minecraftforge.fml.common.discovery.JarDiscoverer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {JarDiscoverer.class}, remap = false)
public abstract class JarDiscovererMixin {

    @Redirect(method = { "discover", "findClassesASM" }, at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"))
    private boolean shouldSkip(String entry, String originalPattern) {

        return entry.startsWith("META-INF/versions/9/") || entry.startsWith(originalPattern);
    }

}
//#endif
