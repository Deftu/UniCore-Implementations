package xyz.unifycraft.unicore.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import xyz.unifycraft.unicore.api.utils.ChatHelper

class ChatHelperImpl : ChatHelper {
    private val regexes = mutableListOf<Regex>()

    fun initialize() {
        addPlayerRegex("<(?<username>\\w{3,16})>".toRegex()) // Default game chat
        addPlayerRegex("(?<username>\\w{3,16}): ".toRegex()) // More common chat format
        addPlayerRegex("(?<username>\\w{3,16}) >".toRegex()) // Semi-common chat format
    }

    override fun sendMessage(message: IChatComponent) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(message)
    }
    override fun sendMessage(message: String) = sendMessage(ChatComponentText(message))

    override fun retrievePlayer(text: IChatComponent): AbstractClientPlayer? {
        val text = text.unformattedText
        val result = regexes.map {
            it.find(text)
        }.firstOrNull() ?: return null
        val group = result.groups["username"] ?: return null
        val username = group.value
        val player = Minecraft.getMinecraft().theWorld.playerEntities.firstOrNull {
            it.name == username
        } ?: return null
        if (player !is AbstractClientPlayer) return null
        return player
    }

    override fun retrievePlayer(text: String) = retrievePlayer(ChatComponentText(text))
    override fun addPlayerRegex(regex: Regex) { regexes.add(regex) }
    override fun addPlayerRegex(regex: String) = addPlayerRegex(regex.toRegex())
}
