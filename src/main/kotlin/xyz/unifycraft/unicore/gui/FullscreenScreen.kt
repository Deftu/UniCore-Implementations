package xyz.unifycraft.unicore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import xyz.unifycraft.unicore.api.UniCore
import java.awt.Color

abstract class FullscreenScreen(
    withLogo: Boolean = false,
    enableRepeatKeys: Boolean = true,
    drawDefaultBackground: Boolean = true,
    restoreCurrentGuiOnClose: Boolean = false,
    newGuiScale: Int = -1
) : WindowScreen(
    version = ElementaVersion.V1,
    enableRepeatKeys,
    drawDefaultBackground,
    restoreCurrentGuiOnClose,
    newGuiScale
) {
    private val background = UIBlock(color).constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf window
    val logo = UIImage.ofResourceCached("/assets/unicore/logo.png", UniCore.getElementaResourceCache()).constrain {
        x = 10.pixels()
        y = 10.pixels()
        width = 50.pixels()
        height = 50.pixels()
    } childOf window
    val container = UIContainer().constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf window

    init {
        if (!withLogo) logo.hide(true)
    }

    companion object {
        @JvmStatic
        val color = Color(14, 4, 27)
    }
}
