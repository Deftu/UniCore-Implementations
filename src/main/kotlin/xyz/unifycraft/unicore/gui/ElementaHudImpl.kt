package xyz.unifycraft.unicore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.universal.UMatrixStack
import me.kbrewster.eventbus.Subscribe
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.events.*
import xyz.unifycraft.unicore.api.events.input.KeyboardInputEvent
import xyz.unifycraft.unicore.api.events.input.MouseButtonEvent
import xyz.unifycraft.unicore.api.events.input.MouseMoveEvent
import xyz.unifycraft.unicore.api.events.input.MouseScrollEvent
import xyz.unifycraft.unicore.api.gui.ElementaHud

class ElementaHudImpl : ElementaHud {
    override val window = Window(ElementaVersion.V1)
    override val namespaces = mutableMapOf<String, UIContainer>()

    fun initialize() {
        Inspector(window) childOf window
        UniCore.getEventBus().register(this)
    }

    override fun <T : UIComponent> add(namespace: String, component: T, createIfNotExists: Boolean) {
        val container = namespace(namespace, createIfNotExists)
        println("Children: ${container.children.toList()}")
        component childOf container
        namespaces[namespace] = container
    }

    override fun namespace(namespace: String, createIfNotExists: Boolean) =
        if (createIfNotExists) namespaces.getOrPut(namespace) { createNamespaceContainer() } else namespaces[namespace]!!

    private fun createNamespaceContainer() = UIContainer().constrain {
        width = RelativeConstraint()
        height = RelativeConstraint()
    } childOf window

    @Subscribe
    fun onRenderTick(event: RenderTickEvent) {
        window.draw(UMatrixStack())
    }

    @Subscribe
    fun onMouseScroll(event: MouseScrollEvent) {
        window.mouseScroll(event.delta)
    }

    @Subscribe
    fun onMouseMove(event: MouseMoveEvent) {
        window.mouseMove(window)
    }

    @Subscribe
    fun onMouseClick(event: MouseButtonEvent) {
        if (!event.released) window.mouseClick(event.x, event.y, event.button)
        else window.mouseRelease()
    }

    @Subscribe
    fun onKeyboardInput(event: KeyboardInputEvent) {
        window.keyType(event.typedChar, event.keyCode)
    }
}
