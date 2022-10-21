package xyz.unifycraft.unicore.gui.notifications

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import xyz.unifycraft.unicore.api.UniCorePalette
import xyz.unifycraft.unicore.gui.components.UIHoverBlock

class Notification(
    val title: String,
    val content: String,
    private val duration: Float,
    val action: Runnable
) : UIHoverBlock(
    color = UniCorePalette.BACKGROUND.toConstraint(),
    hoverColor = UniCorePalette.PRIMARY_VARIANT,
    originalColor = UniCorePalette.PRIMARY,
    outlineWidth = 1f
) {
    private val progressBar = UIBlock(UniCorePalette.PRIMARY).constrain {
        x = 0.pixels()
        y = 0.pixels(true)
        width = 0.pixels()
        height = 5.pixels()
    } childOf this

    init {
        constrain {
            x = 0.pixels(alignOpposite = true, alignOutside = true)
            y = 2.5f.pixels()
            width = Notification.width.pixels()
            height = Notification.height.pixels()
        }.onMouseEnter {
            highlight()
        }.onMouseLeave {
            unhighlight()
        }.onMouseRelease {
            action.run()
            animateOut()
        }

        UIWrappedText(title).constrain {
            x = 2.pixels()
            y = 2.pixels()
            width = FillConstraint(false) - 10.pixels()
            textScale = 1.3f.pixels()
        } childOf this
        val contentText = UIWrappedText(content).constrain {
            x = 2.pixels()
            y = SiblingConstraint(2f)
            width = FillConstraint(false) - 8.pixels()
        } childOf this

        constrain {
            height = (90.percent() boundTo contentText) + constraints.height
        }
    }

    override fun afterInitialization() {
        animateIn()
    }

    private fun highlight() {
        progressBar.animate {
            setColorAnimation(Animations.OUT_EXP, 1f, this@Notification.hoverColor.brighter().toConstraint())
        }
    }

    private fun unhighlight() {
        progressBar.animate {
            setColorAnimation(Animations.OUT_EXP, 1f, this@Notification.hoverColor.toConstraint())
        }
    }

    private fun animateIn() {
        animate {
            setXAnimation(
                movementAnimation,
                movementDuration,
                2.5f.pixels(true)
            )

            onComplete {
                animateProgress()
            }
        }
    }

    private fun animateProgress() {
        progressBar.animate {
            setWidthAnimation(
                progressAnimation,
                duration,
                FillConstraint(false)
            )

            onComplete {
                animateOut()
            }
        }
    }

    private fun animateOut() {
        animate {
            setXAnimation(
                movementAnimation,
                movementDuration,
                0.pixels(alignOpposite = true, alignOutside = true)
            )

            onComplete {
                hide(true)
            }
        }
    }

    companion object {
        @JvmStatic val width = 150
        @JvmStatic val height = 50
        @JvmStatic val movementAnimation = Animations.IN_OUT_QUAD
        @JvmStatic val movementDuration = 0.75f
        @JvmStatic val progressAnimation = Animations.LINEAR
    }
}