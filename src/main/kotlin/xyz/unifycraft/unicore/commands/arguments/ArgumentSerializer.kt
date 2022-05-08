package xyz.unifycraft.unicore.commands.arguments

import xyz.unifycraft.unicore.api.commands.arguments.ArgumentQueue
import java.lang.reflect.Parameter

interface ArgumentSerializer<T> {
    fun parse(queue: ArgumentQueue, parameter: Parameter): T
}
