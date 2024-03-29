package xyz.unifycraft.unicore.api.commands.arguments

import xyz.unifycraft.unicore.commands.arguments.ArgumentSerializer
import java.lang.reflect.Parameter

class FloatArgumentSerializer : ArgumentSerializer<Float> {
    override fun parse(queue: ArgumentQueue, parameter: Parameter): Float {
        return queue.poll().toFloat()
    }
}