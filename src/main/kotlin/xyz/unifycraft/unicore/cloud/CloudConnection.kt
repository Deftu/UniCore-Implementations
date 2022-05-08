package xyz.unifycraft.unicore.cloud

import com.google.gson.JsonObject
import gg.essential.universal.UMinecraft
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.java_websocket.handshake.ServerHandshake
import xyz.deftu.quicksocket.client.QuickSocketClient
import xyz.deftu.quicksocket.common.CloseCode
import xyz.deftu.quicksocket.common.packets.PacketBase
import xyz.unifycraft.unicore.onboarding.Onboarding
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.UniCoreEnvironment
import xyz.unifycraft.unicore.cloud.packets.PacketKeepAlive
import xyz.unifycraft.unicore.cloud.packets.PacketNewRelease
import xyz.unifycraft.unicore.utils.http.HttpRequesterImpl
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class CloudConnection(
    private var sessionId: UUID,
    vararg headers: Pair<String, String>
) : QuickSocketClient(
    uri = UniCoreEnvironment.cloudConnectionUri,
    headers = (headers.toMap() + mapOf(proposedSessionIdName to sessionId.toString()))
) {
    private val connectLock = ReentrantLock()
    private var previouslyConnected = false
    private val logger = LogManager.getLogger("${UniCore.getName()} (Cloud Connection)")

    init {
        addPacket("KEEP_ALIVE", PacketKeepAlive::class.java)
        addPacket("NEW_RELEASE", PacketNewRelease::class.java)
    }

    fun tryConnect() {
        try {
            if (!connectLock.tryLock()) return
            if (!Onboarding.isToS()) return
            if (isOpen) return
            addHeader("minecraft_token", UMinecraft.getMinecraft().session.token)
            if (!previouslyConnected) {
                if (uri.scheme == "wss")
                    setSocketFactory(HttpRequesterImpl.sslSocketFactory)
                connectBlocking(10, TimeUnit.SECONDS)
                previouslyConnected = true
            } else reconnectBlocking()
        } catch (e: Exception) {
            previouslyConnected = false
            logger.error("Failed to connect to the ${UniCore.getName()} Cloud.", e)
        } finally {
            connectLock.unlock()
        }
    }

    override fun connect() {
        super.connect()
        previouslyConnected = true
    }

    override fun onConnectionOpened(handshake: ServerHandshake) {
        if (!handshake.hasFieldValue(acceptedSessionIdName)) throw IllegalConnectionException("The connection that the UniCore cloud connected to did not provide a session ID.")
        val acceptedSessionId = handshake.getFieldValue(acceptedSessionIdName).toBoolean()
        if (!acceptedSessionId) runBlocking {
            if (!handshake.hasFieldValue(sessionIdName)) throw IllegalArgumentException("The connection did not accept the proposed session ID, but it did not provide a new one.")
            sessionId = UUID.fromString(handshake.getFieldValue(sessionIdName))
        }

        logger.info("Successfully established connection to ${UniCore.getName()} Cloud!")
    }

    override fun onConnectionClosed(code: CloseCode, reason: String, remote: Boolean) {
        if (code == CloseCode.PROTOCOL_ERROR)
            UniCore.getNotifications().post("${UniCore.getName()} Cloud", "Failed to connect to the UniCore Cloud.")
        logger.warn("Connection to ${UniCore.getName()} Cloud closed. ($code - $reason)")
    }

    override fun onPacketSent(packet: PacketBase, data: JsonObject) =
        data.addProperty(sessionIdName, sessionId.toString())

    companion object {
        const val proposedSessionIdName = "proposed_session_id"
        const val acceptedSessionIdName = "accepted_session_id"
        const val sessionIdName = "session_id"
    }
}
