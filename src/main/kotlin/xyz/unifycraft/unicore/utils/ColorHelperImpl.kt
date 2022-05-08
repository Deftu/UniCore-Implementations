package xyz.unifycraft.unicore.utils

import xyz.unifycraft.unicore.api.utils.ColorHelper
import java.awt.Color

class ColorHelperImpl : ColorHelper {
    override fun getChroma() = Color.HSBtoRGB(System.currentTimeMillis() % 2000L / 2000.0f, 1.0f, 1.0f)
    override fun getRed(rgba: Int) = (rgba shr 16) and 0xFF
    override fun getGreen(rgba: Int) = (rgba shr 8) and 0xFF
    override fun getBlue(rgba: Int) = (rgba shr 0) and 0xFF
    override fun getAlpha(rgba: Int) = (rgba shr 24) and 0xFF
}
