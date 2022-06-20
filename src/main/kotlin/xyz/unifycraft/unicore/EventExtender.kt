package xyz.unifycraft.unicore

import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.events.CancellableEvent
import xyz.unifycraft.unicore.api.events.input.KeyboardInputEvent
import xyz.unifycraft.unicore.api.events.input.MouseButtonEvent
import xyz.unifycraft.unicore.api.events.input.MouseMoveEvent
import xyz.unifycraft.unicore.api.events.input.MouseScrollEvent
import xyz.unifycraft.unicore.event.MouseInputHandler

class EventExtender {

    @SubscribeEvent
    fun onKeyboardInput(event: GuiScreenEvent.KeyboardInputEvent) {
        val repeatEvents = Keyboard.areRepeatEventsEnabled()
        Keyboard.enableRepeatEvents(true)
        val event: CancellableEvent = KeyboardInputEvent(
            Keyboard.getEventKeyState(),
            Keyboard.isRepeatEvent(),
            Keyboard.getEventCharacter(),
            Keyboard.getEventKey()
        )
        UniCore.getEventBus().post(event)
        Keyboard.enableRepeatEvents(repeatEvents)
        if (event.cancelled) {
            event.cancelled = true
        }
    }

    @SubscribeEvent
    fun onMouseInput(event: GuiScreenEvent.MouseInputEvent.Pre) {
        event.isCanceled = MouseInputHandler.handle()
    }

}
