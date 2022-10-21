package xyz.unifycraft.unicore.cloud.packets

import com.google.gson.JsonObject
import xyz.deftu.quicksocket.common.packets.PacketBase
import xyz.unifycraft.unicore.api.UniCore

class PacketNewRelease : PacketBase("NEW_RELEASE") {
    override fun onPacketReceived(data: JsonObject?) {
        if (data == null) return
        val version = data["version"].asJsonPrimitive.asString
        if (version.isBlank()) return
        UniCore.getNotifications().post(UniCore.getName(), "New release available: $version", this::onNewReleaseInteract)
    }

    override fun onPacketSent(data: JsonObject) {
        throw UnsupportedOperationException("We cannot send new release packets to the server.")
    }

    private fun onNewReleaseInteract() {
        // TODO: Open update menu and close the game when the user clicks on it.
    }
}