package xyz.unifycraft.unicore.rpc

import xyz.unifycraft.unicore.api.UniCore
import java.io.File

class DiscordHandler {
    fun initialize() {
        NativeDownloader.download(File(UniCore.getFileHelper().dataDir, "Discord"))
    }
}
