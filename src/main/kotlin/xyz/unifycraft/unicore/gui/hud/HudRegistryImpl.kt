package xyz.unifycraft.unicore.gui.hud

import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.gui.hud.HudElement
import xyz.unifycraft.unicore.api.gui.hud.HudElementMetadata
import xyz.unifycraft.unicore.api.gui.hud.HudRegistry

class HudRegistryImpl : HudRegistry {
    private val elements = mutableListOf<HudElement>()
    val config = HudRegistryConfig()

    override fun registerElement(element: HudElementMetadata) {
        val created = element.create()
        load(created)
        elements.add(created)
        UniCore.getElementaHud().add(NAMESPACE, created)
    }

    private fun load(element: HudElement) {
        config.initialize()
        val theConfig = config.load(element)
        element.constrain {
            x = theConfig.x.percent()
            y = theConfig.y.percent()
        }
    }

    companion object {
        const val NAMESPACE = "hud_registry"
    }
}
