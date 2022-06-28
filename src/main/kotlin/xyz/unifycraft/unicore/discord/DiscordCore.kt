package xyz.unifycraft.unicore.discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import net.minecraft.client.Minecraft
import xyz.unifycraft.unicore.api.UniCore
import java.io.File
import java.time.Instant

class DiscordCore {
    lateinit var core: Core
        private set
    var successful = false
        private set
    private var startTime: Long = 0

    private lateinit var callbacks: Thread

    fun initialize() {
        NativeDownloader.download(File(UniCore.getFileHelper().dataDir, "Discord"))
        if (!NativeDownloader.successful) return

        val params = CreateParams()
        params.clientID = 990541367316996146
        params.flags = CreateParams.getDefaultFlags()
        params.registerEventHandler(DiscordEventHandler)
        try {
            core = Core(params)
            successful = true
            start()

            if (!::callbacks.isInitialized) {
                callbacks = Thread({
                    while (successful) {
                        try {
                            core.runCallbacks()
                            Thread.sleep(16) // Save a little CPU
                        } catch (e: Exception) {
                            UniCore.getLogger().error("Error running Discord callbacks", e)
                        }
                    }
                }, "Discord Callbacks").apply {
                    start()
                }
            }
        } catch (e: Exception) {
            UniCore.getLogger().error("Failed to initialize Discord core", e)
            successful = false
        }

        params.close()
    }

    private fun start() {
        startTime = Instant.now().toEpochMilli()
        updateActivity(Activity().apply {
            details = "Starting up..."
        })
    }

    fun updateActivity(activity: Activity) {
        if (!successful) return
        activity.assets().largeImage = "logo"
        activity.timestamps().start = Instant.ofEpochMilli(startTime)
        val username =
            //#if MC<=11404
            Minecraft.getMinecraft().session.username
            //#else
            //$$ // TODO - MC 1.15+
            //#endif
        activity.state = username
        core.activityManager().updateActivity(activity)
        activity.close()
    }
}
