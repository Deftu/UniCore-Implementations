package xyz.unifycraft.unicore.gui.notifications

import gg.essential.elementa.utils.ObservableRemoveEvent
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.gui.notifications.NotificationData
import xyz.unifycraft.unicore.api.gui.notifications.Notifications
import xyz.unifycraft.unicore.api.gui.ofHud
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

class NotificationsImpl : Notifications {
    private val namespace = "${UniCore.getName()} Notifications"
    private val queue = LinkedBlockingDeque<Notification>()

    override val history = mutableListOf<NotificationData>()

    init {
        UniCore.getElementaHud().namespace(namespace).children.addObserver { _, event ->
            if (event !is ObservableRemoveEvent<*>) return@addObserver
            val value = event.element.value
            if (value == null || value !is Notification) return@addObserver
            val queued = queue.poll() ?: return@addObserver
            post(queued)
        }
    }

    fun post(notification: Notification) {
        if (UniCore.getElementaHud().namespace(namespace).childrenOfType(Notification::class.java).isEmpty()) {
            history.add(NotificationData(notification.title, notification.content, notification.action))
            notification ofHud namespace
        } else queue.add(notification)
    }

    override fun post(title: String, content: String) =
        post(Notification(title, content, Notifications.DEFAULT_DURATION) { })
    override fun post(title: String, content: String, action: Runnable) =
        post(Notification(title, content, Notifications.DEFAULT_DURATION, action))

    override fun post(title: String, content: String, duration: Float) =
        post(Notification(title, content, duration) { })
    override fun post(title: String, content: String, duration: Float, action: Runnable) =
        post(Notification(title, content, duration, action))
}
