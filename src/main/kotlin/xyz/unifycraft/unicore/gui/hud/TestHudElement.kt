package xyz.unifycraft.unicore.gui.hud

import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import xyz.unifycraft.unicore.api.gui.hud.HudElement
import xyz.unifycraft.unicore.api.gui.hud.HudElementMetadata

class TestHudElement : HudElement(TestHudElement) {
    val text = UIText("Test") childOf this

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }
    }

    companion object : HudElementMetadata() {
        override fun create() = TestHudElement()
        override fun getName() = "Test"
    }
}