package xyz.unifycraft.unicore.gui.components

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

open class UIHoverBlock(
    color: ColorConstraint = Color.WHITE.toConstraint(),
    val hoverColor: Color = Color.WHITE,
    val originalColor: Color = Color(0, 0, 0, 0),
    val animation: Animations = Animations.OUT_EXP,
    val animationTime: Float = 0.75f,
    outlineWidth: Float = 2f,
    drawAfterChildren: Boolean = false,
    drawInsideChildren: Boolean = false,
    sides: Set<OutlineEffect.Side> = setOf(
        OutlineEffect.Side.Left,
        OutlineEffect.Side.Top,
        OutlineEffect.Side.Right,
        OutlineEffect.Side.Bottom
    )
) : UIBlock(
    color
) {
    init {
        val effect = OutlineEffect(originalColor, outlineWidth, drawAfterChildren, drawInsideChildren, sides)
        effect(effect).onMouseEnter {
            effect::color.animate(animation, animationTime, hoverColor)
        }.onMouseLeave {
            effect::color.animate(animation, animationTime, originalColor)
        }
    }
}
