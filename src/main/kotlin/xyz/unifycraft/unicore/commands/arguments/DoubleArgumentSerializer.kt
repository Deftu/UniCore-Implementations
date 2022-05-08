package xyz.unifycraft.unicore.api.commands.arguments

import xyz.unifycraft.unicore.commands.arguments.ArgumentSerializer
import java.lang.reflect.Parameter

class DoubleArgumentSerializer : ArgumentSerializer<Double> {
    override fun parse(queue: ArgumentQueue, parameter: Parameter): Double {
        return queue.poll().toDouble()
    }
}