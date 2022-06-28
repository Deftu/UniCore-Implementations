package xyz.unifycraft.unicore.event

import gg.essential.universal.UMouse
import net.minecraft.client.Minecraft
import org.lwjgl.input.Mouse
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.events.CancellableEvent
import xyz.unifycraft.unicore.api.events.input.MouseButtonEvent
import xyz.unifycraft.unicore.api.events.input.MouseMoveEvent
import xyz.unifycraft.unicore.api.events.input.MouseScrollEvent
import kotlin.math.floor

object MouseInputHandler {
    fun handle(): Boolean {
        //#if MC<=11202
        val isScrolled = Mouse.getEventDWheel() != 0
        if (!isScrolled) {
            val isButton = Mouse.getEventButton() >= 0
            if (isButton) {
                val event: CancellableEvent = MouseButtonEvent(
                    Mouse.getEventButton(),
                    !Mouse.getEventButtonState(),
                    UMouse.Scaled.x,
                    UMouse.Scaled.y
                )
                UniCore.getEventBus().post(event)
                if (event.cancelled) {
                    return true
                }
            } else UniCore.getEventBus().post(
                MouseMoveEvent(
                    Mouse.getEventX().toDouble(),
                    Mouse.getEventY().toDouble()
                )
            )
        } else UniCore.getEventBus().post(MouseScrollEvent(Mouse.getEventDWheel().toDouble()))
        return false
        //#else
        //$$ // TODO
        //#endif
    }
}
