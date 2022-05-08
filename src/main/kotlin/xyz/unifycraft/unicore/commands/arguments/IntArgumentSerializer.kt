package xyz.unifycraft.unicore.api.commands.arguments

import xyz.unifycraft.unicore.commands.arguments.ArgumentSerializer
import java.lang.reflect.Parameter

class IntArgumentSerializer : ArgumentSerializer<Int> {
    override fun parse(queue: ArgumentQueue, parameter: Parameter): Int {
        return queue.poll().toInt()
    }
}