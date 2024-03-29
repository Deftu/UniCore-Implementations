package xyz.unifycraft.unicore.commands

import gg.essential.universal.ChatColor
import me.kbrewster.eventbus.Subscribe
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.commands.BaseCommand
import xyz.unifycraft.unicore.api.commands.CommandRegistry
import xyz.unifycraft.unicore.api.commands.annotations.Command
import xyz.unifycraft.unicore.api.commands.arguments.*
import xyz.unifycraft.unicore.api.events.chat.ChatSendEvent
import xyz.unifycraft.unicore.commands.arguments.ArgumentSerializer

class CommandRegistryImpl : CommandRegistry {
    val argumentSerializers = mutableMapOf<Class<*>, ArgumentSerializer<*>>()
    override val commands = mutableMapOf<String, BaseCommand>()
    private var autoCompletion: Array<String>? = null

    init {
        UniCore.getEventBus().register(this)

        argumentSerializers[Boolean::class.java] = BooleanArgumentSerializer()
        argumentSerializers[Double::class.java] = DoubleArgumentSerializer()
        argumentSerializers[Float::class.java] = FloatArgumentSerializer()
        argumentSerializers[Int::class.java] = IntArgumentSerializer()
        argumentSerializers[String::class.java] = StringArgumentSerializer()
    }

    override fun registerCommand(command: BaseCommand) {
        commands[command.name] = command
        command.aliases.forEach { commands[it] = command }
    }

    override fun registerCommand(command: Any) = registerCommand(AnnotationCommand(argumentSerializers, command::class.java.getAnnotation(
        Command::class.java), command::class.java, command) as BaseCommand)

    override fun processAutoComplete(input: String) {
        autoCompletion = null
        if (!input.startsWith("/")) return
        val input = input.replaceFirst("/", "")
        if (Minecraft.getMinecraft().currentScreen is GuiChat) {
            val options = retrieveAutoCompletions(input).toMutableList()
            if (options.isNotEmpty()) {
                if (input.indexOf(' ') == -1) {
                    for (i in options.indices) {
                        options[i] = ChatColor.BOLD + "/" + options[i] + ChatColor.RESET
                    }
                } else {
                    for (i in options.indices) {
                        options[i] = ChatColor.GRAY + options[i] + ChatColor.RESET
                    }
                }
                autoCompletion = options.toTypedArray()
            }
        }
    }

    private fun retrieveAutoCompletions(input: String): List<String> {
        val split = input.split(" ")
        val first = split[0]
        return if (split.size == 1) {
            val value = mutableListOf<String>()
            for (command in commands) {
                if (command.key.startsWith(first)) {
                    value.add(command.key)
                }
            }

            value
        } else listOf()
    }

    override fun getAutoCompletion() = autoCompletion ?: arrayOf()

    @Subscribe private fun onChatSent(event: ChatSendEvent) {
        var message = event.message.trim()
        if (!message.startsWith("/")) return

        message = message.replaceFirst("/", "")
        val split = message.split(" ")
        val name = split[0]
        if (!commands.containsKey(name)) return

        val args = split.subList(1, split.size)
        val command = commands[name]!!
        command.execute(args.toList())
        event.cancelled = true
    }
}