package xyz.unifycraft.unicore.gui.notifications

import gg.essential.elementa.utils.ObservableRemoveEvent
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.gui.notifications.Notifications
import xyz.unifycraft.unicore.api.gui.ofHud
import java.util.concurrent.LinkedBlockingDeque

class NotificationsImpl : Notifications {
    private val namespace = "${UniCore.getName()} Notifications"
    private val queue = LinkedBlockingDeque<Notification>()

    init {
        UniCore.getElementaHud().namespace(namespace).children.addObserver { _, event ->
            if (event is ObservableRemoveEvent<*>) {
                val element = event.element
                if (element.value != null && element.value is Notification) {
                    val queued = queue.poll()
                    if (queued != null) post(queued)
                }
            }
        }
    }

    fun post(notification: Notification) {
        if (UniCore.getElementaHud().namespace(namespace).childrenOfType(Notification::class.java).isEmpty()) {
            notification ofHud namespace
        } else queue.add(notification)
    }
    override fun post(title: String, description: String) =
        post(Notification(title, description, Notifications.DEFAULT_DURATION) { })
    override fun post(title: String, description: String, duration: Float) =
        post(Notification(title, description, duration) { })
    override fun post(title: String, description: String, duration: Float, click: Runnable) =
        post(Notification(title, description, duration, click))
}
