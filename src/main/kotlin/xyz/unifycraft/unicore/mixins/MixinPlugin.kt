package xyz.unifycraft.unicore.mixins

import org.spongepowered.asm.lib.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class MixinPlugin : IMixinConfigPlugin {
    // We won't be needing any of these.
    override fun onLoad(mixinPackage: String) {  }
    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String) = true
    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {  }
    override fun preApply(p0: String, p1: ClassNode, p2: String, p3: IMixinInfo) {  }
    override fun postApply(p0: String, p1: ClassNode, p2: String, p3: IMixinInfo) {  }

    // Specify our pre-processed Mixin configs.
    override fun getRefMapperConfig() = "mixins.unicore.refmap.json"
    override fun getMixins() = mutableListOf(
        //#if MC<=11202
        "client.MinecraftMixin",
        "gui.GuiChatMixin",
        "gui.GuiScreenMixin"
        //#elseif MC<=11701
        //$$ "..."
        //#endif
    )
}