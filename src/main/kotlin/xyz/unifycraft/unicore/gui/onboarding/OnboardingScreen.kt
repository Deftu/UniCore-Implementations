package xyz.unifycraft.unicore.gui.onboarding

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.universal.GuiScale
import xyz.unifycraft.unicore.gui.FullscreenScreen
import java.awt.Color

class OnboardingScreen : FullscreenScreen(
    newGuiScale = GuiScale.scaleForScreenSize().ordinal
) {
    // Pagination
    private val pages = listOf(
        OnboardingPageWelcome(),
        OnboardingPageAgreement()
    )
    private lateinit var currentPage: OnboardingPageBase

    // Animating
    private val fade = UIBlock().constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf window

    init {
        switchPage(0)
    }

    fun switchPage(index: Int) {
        if (index < 0 || index >= pages.size)
            return
        fadeOut()
        withCurrentPage { hide() }
        val page = pages[index]
        page.constrain {
            x = CenterConstraint()
            height = 80.percent()
            width = 80.percent()
        } childOf container
        fadeIn()
    }

    fun fadeIn() {
        fade.animate {
            setColorAnimation(Animations.LINEAR, 0.5f, Color(0f, 0f, 0f, 0f).toConstraint())
        }
    }

    fun fadeOut() {
        fade.animate {
            setColorAnimation(Animations.LINEAR, 0.5f, Color.BLACK.toConstraint())
        }
    }

    private fun withCurrentPage(block: OnboardingPageBase.() -> Unit) {
        if (::currentPage.isInitialized)
            block(currentPage)
    }
}
