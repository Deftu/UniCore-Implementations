package xyz.unifycraft.unicore.utils.text

import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.IChatComponent
import xyz.unifycraft.unicore.api.utils.text.Text
import xyz.unifycraft.unicore.api.utils.text.TextHelper

class TextHelperImpl : TextHelper {
    override fun create(text: String) = TextImpl(text)
    override fun toVanilla(text: Text) =
        //#if MC<=14404
        toVanillaLegacy(text)
        //#else
        //$$ toVanillaModern(text)
        //#endif

    //#if MC<=14404
    private fun toVanillaLegacy(text: Text): IChatComponent {
        val component = ChatComponentTranslation(text.asFormattedString())
        // Uhhh I'll do link formatting in a bit...
        return component
    }
    //#else
    //$$ private fun toVanillaModern(text: Text): net.minecraft.text.Text {
    //$$
    //$$ }
    //#endif
}
