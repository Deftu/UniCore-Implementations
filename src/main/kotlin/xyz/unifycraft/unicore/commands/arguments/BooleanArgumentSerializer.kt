package xyz.unifycraft.unicore.api.commands.arguments

import xyz.unifycraft.unicore.commands.arguments.ArgumentSerializer
import java.lang.reflect.Parameter

class BooleanArgumentSerializer : ArgumentSerializer<Boolean> {
    override fun parse(queue: ArgumentQueue, parameter: Parameter): Boolean {
        return queue.poll().contentEquals("true", true)
    }
}