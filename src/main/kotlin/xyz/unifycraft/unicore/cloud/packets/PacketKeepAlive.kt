package xyz.unifycraft.unicore.cloud.packets

import com.google.gson.JsonObject
import xyz.deftu.deftils.Multithreading
import xyz.deftu.quicksocket.common.packets.PacketBase
import xyz.unifycraft.unicore.UniCoreImpl
import java.util.concurrent.TimeUnit

class PacketKeepAlive : PacketBase("KEEP_ALIVE") {
    override fun onPacketReceived(data: JsonObject?) {
        if (data == null) return
        Multithreading.schedule({
            println("Sending keep alive packet...")
            UniCoreImpl.instance.cloudConnection().sendPacket(PacketKeepAlive())
        }, data["interval"].asJsonPrimitive.asLong, TimeUnit.MILLISECONDS)
    }

    override fun onPacketSent(data: JsonObject) {
        // We won't use this for this packet!
    }
}
