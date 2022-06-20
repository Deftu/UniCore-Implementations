package xyz.unifycraft.unicore.utils

import me.kbrewster.eventbus.Subscribe
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.events.TickEvent
import xyz.unifycraft.unicore.api.utils.MessageQueue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer

class MessageQueueImpl : MessageQueue {
    private val queue  = ConcurrentLinkedQueue<MessageQueueEntry>()
    private var ticks = 0

    fun initialize() {
        UniCore.getEventBus().register(this)
    }

    override fun queue(text: () -> String) { queue.add(MessageQueueEntry(text.invoke(), null, null)) }
    override fun queue(text: () -> String, callback: Consumer<String>) { queue.add(MessageQueueEntry(text.invoke(), null, callback)) }
    override fun queue(text: () -> String, delay: Int) {  queue.add(MessageQueueEntry(text.invoke(), delay, null)) }
    override fun queue(text: () -> String, delay: Int, callback: Consumer<String>) {  queue.add(MessageQueueEntry(text.invoke(), delay, callback)) }

    override fun queue(text: String) { queue.add(MessageQueueEntry(text, null, null)) }
    override fun queue(text: String, callback: Consumer<String>) {  queue.add(MessageQueueEntry(text, null, callback)) }
    override fun queue(text: String, delay: Int) {  queue.add(MessageQueueEntry(text, delay, null)) }
    override fun queue(text: String, delay: Int, callback: Consumer<String>) { queue.add(MessageQueueEntry(text, delay, callback)) }

    private fun handleNext(entry: MessageQueueEntry) {
        val delay = entry.delay ?: MessageQueue.DEFAULT_DELAY
        if (delay % ticks != 0) return
        UniCore.getChatHelper().sendMessage(entry.text)
        entry.callback?.accept(entry.text)
        queue.remove(entry)
        ticks = 0
    }

    @Subscribe
    fun onTick(event: TickEvent) {
        ticks++
        if (queue.isNotEmpty()) handleNext(queue.element())
        if (ticks >= MINUTE) ticks = 0
    }

    companion object {
        const val MINUTE = 1200
    }
}

internal class MessageQueueEntry(
    val text: String,
    val delay: Int?,
    val callback: Consumer<String>?
)
