package xyz.unifycraft.unicore.utils.hypixel

import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import xyz.deftu.quicksocket.common.utils.QuickSocketJsonHandler
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.utils.hypixel.HypixelGameType
import xyz.unifycraft.unicore.api.utils.hypixel.HypixelHelper
import xyz.unifycraft.unicore.api.utils.hypixel.HypixelLocraw
import xyz.unifycraft.unicore.api.utils.hypixel.HypixelLocrawHelper
import xyz.unifycraft.unicore.api.events.HypixelLocationChangeEvent
import java.util.concurrent.TimeUnit

class HypixelLocrawHelperImpl(
    private val hypixelHelper: HypixelHelper
) : HypixelLocrawHelper {
    override var locraw: HypixelLocraw? = null
    private var tickCounter = 0
    private var sendPermitted = false
    private var checked = false
    private var limboLoop = 0

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun enqueueUpdate(interval: Long) {
        sendPermitted = true
        UniCore.getMultithreader().schedule({
            if (sendPermitted) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw")
            }
        }, interval, TimeUnit.MILLISECONDS)
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        tickCounter++
        if (tickCounter % 20 == 0) {
            tickCounter = 0
            if (hypixelHelper.isHypixel() && !checked) {
                enqueueUpdate(500)
                checked = true
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        locraw = null
        sendPermitted = false
        checked = false
        limboLoop = 0
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    fun onChatMessageReceived(event: ClientChatReceivedEvent) {
        if (!checked) return
        val stripped = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.unformattedText)
        if (!QuickSocketJsonHandler.parser.isValidJson(stripped)) {
            if (stripped.contains("You are sending too many commands! Please try again in a few seconds.")) {
                enqueueUpdate(5000)
                return
            } else return
        }

        val raw = UniCore.getJsonHelper().parse(stripped)
        if (!raw.isJsonObject) return
        val json = raw.asJsonObject
        val parsed = UniCore.getJsonHelper().gson.fromJson(json, HypixelLocraw::class.java) ?: return
        if (parsed.gameType == HypixelGameType.LIMBO) {
            checked = false
            limboLoop++
            enqueueUpdate(1000)
        } else locraw = parsed
        UniCore.getEventBus().post(HypixelLocationChangeEvent(parsed))
        event.isCanceled = true
    }
}