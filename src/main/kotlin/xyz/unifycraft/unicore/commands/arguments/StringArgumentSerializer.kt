package xyz.unifycraft.unicore.api.commands.arguments

import xyz.unifycraft.unicore.api.commands.annotations.Greedy
import xyz.unifycraft.unicore.commands.arguments.ArgumentSerializer
import java.lang.reflect.Parameter

class StringArgumentSerializer : ArgumentSerializer<String> {
    override fun parse(queue: ArgumentQueue, parameter: Parameter): String {
        return if (parameter.isAnnotationPresent(Greedy::class.java)) {
            val compiled = mutableListOf<String>()
            while (!queue.isEmpty()) compiled.add(queue.poll())
            compiled.joinToString(" ")
        } else queue.poll()
    }
}