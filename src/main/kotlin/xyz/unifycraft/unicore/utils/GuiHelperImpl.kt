package xyz.unifycraft.unicore.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import xyz.unifycraft.unicore.api.utils.GuiHelper
//#if MC<=11202 && FORGE==1
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

//#endif

class GuiHelperImpl : GuiHelper {
    //#if MC<=11202
    override var screen: GuiScreen? = null
    //#endif

    init {
        //#if MC<=11202
        MinecraftForge.EVENT_BUS.register(this)
        //#endif
    }

    override fun showScreen(screen: GuiScreen) {
        //#if MC<=11202
        this.screen = screen
        MinecraftForge.EVENT_BUS.register(this)
        //#else
        //$$ MinecraftClient.getInstance().setScreen(screen)
        //#endif
    }

    //#if MC<=11202
    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (screen !is PlaceholderScreen) {
            Minecraft.getMinecraft().displayGuiScreen(screen)
            screen = PlaceholderScreen()
            MinecraftForge.EVENT_BUS.unregister(this)
        }
    }

    private class PlaceholderScreen : GuiScreen()
    //#endif
}
