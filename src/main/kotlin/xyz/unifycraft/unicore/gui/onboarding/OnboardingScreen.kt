package xyz.unifycraft.unicore.gui.onboarding

import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.GuiScale
import xyz.unifycraft.unicore.gui.FullscreenScreen

class OnboardingScreen : FullscreenScreen(
    newGuiScale = GuiScale.scaleForScreenSize().ordinal
) {
    private val pages = listOf(
        OnboardingPageWelcome(),
        OnboardingPageAgreement()
    )
    private lateinit var currentPage: OnboardingPageBase

    init {
        switchPage(0)
    }

    fun switchPage(index: Int) {
        if (index < 0 || index >= pages.size)
            return
        withCurrentPage { hide() }
        val page = pages[index]
        page.constrain {
            x = CenterConstraint()
            height = 80.percent()
            width = 80.percent()
        } childOf container
    }

    private fun withCurrentPage(block: OnboardingPageBase.() -> Unit) {
        if (::currentPage.isInitialized)
            block(currentPage)
    }
}
