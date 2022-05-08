package xyz.unifycraft.unicore.onboarding

import gg.essential.universal.UMinecraft
import me.kbrewster.eventbus.Subscribe
import net.minecraft.client.gui.GuiMainMenu
import xyz.unifycraft.unicore.api.events.TickEvent

class OnboardingEventListener {

    @Subscribe
    fun onTick(event: TickEvent) {
        if (UMinecraft.getMinecraft().currentScreen !is GuiMainMenu) return
        if (Onboarding.isOnboardingSeen()) return
        Onboarding.openScreen()
    }

}
