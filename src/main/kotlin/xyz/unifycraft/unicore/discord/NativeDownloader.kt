package xyz.unifycraft.unicore.discord

import de.jcm.discordgamesdk.Core
import okhttp3.Request
import org.apache.commons.lang3.SystemUtils
import org.apache.logging.log4j.LogManager
import xyz.unifycraft.unicore.api.UniCore
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

internal object NativeDownloader {
    val logger = LogManager.getLogger("${UniCore.getName()} (Discord Native Downloader)")

    const val sdkUrl = "https://dl-game-sdk.discordapp.net/3.1.0/discord_game_sdk.zip"
    const val jniUrl = "https://raw.githubusercontent.com/UnifyCraft/PublicData/main/discord_jni.zip"

    var successful = false
        private set

    fun download(directory: File) {
        if (!directory.exists() && !directory.mkdirs()) throw IllegalStateException("Could not create directory ${directory.absolutePath}")
        val sdkFileName = determineSdkFileName()
        val jniFileName = determineJniFileName()
        if (sdkFileName == null || jniFileName == null) {
            logger.error("Could not determine file names for SDK and JNI. This is probably because your OS is not compatible.")
            return
        }
        val sdkFile = File(directory, sdkFileName)
        val jniFile = File(directory, jniFileName)
        if (!sdkFile.exists()) downloadSdk(sdkFile)
        if (!jniFile.exists()) downloadJni(jniFile)
        if (sdkFile.exists() && jniFile.exists()) load(sdkFile, jniFile)
    }

    private fun downloadSdk(file: File) {
        logger.info("Discord SDK not present. Downloading it now.")
        UniCore.getHttpRequester().request(Request.Builder()
            .get()
            .url(sdkUrl)
            .build()) {
            if (!it.isSuccessful) throw IllegalStateException("Failed to download SDK")
            it.body?.byteStream()?.use { input ->
                // Unzip the output
                val stream = ZipInputStream(input)
                var entry: ZipEntry? = null
                while (stream.nextEntry.also {
                    if (it != null) {
                        entry = it
                    }
                } != null) {
                    val architecture = SystemUtils.OS_ARCH.lowercase().let { if (it == "amd64") "x86_64" else it }
                    if (entry?.name == "lib/$architecture/${file.name}") {
                        file.outputStream().use { output ->
                            stream.copyTo(output)
                        }
                        break
                    }
                    stream.closeEntry()
                }
                stream.close()
                logger.info("Discord SDK downloaded successfully.")
            }
        }
    }

    private fun downloadJni(file: File) {
        logger.info("Discord JNI not present. Downloading it now.")
        UniCore.getHttpRequester().request(Request.Builder()
            .get()
            .url(jniUrl)
            .build()) {
            if (!it.isSuccessful) throw IllegalStateException("Failed to download JNI")
            it.body?.byteStream()?.use { input ->
                file.outputStream().use { output ->
                    // Unzip the output
                    val stream = ZipInputStream(input)
                    var entry: ZipEntry? = null
                    while (stream.nextEntry.also {
                            if (it != null) {
                                entry = it
                            }
                        } != null) {
                        val os = if (SystemUtils.IS_OS_WINDOWS) "windows" else if (SystemUtils.IS_OS_MAC) "macos" else "linux"
                        val architecture = SystemUtils.OS_ARCH.lowercase()
                        println("OS: $os")
                        println("Architecture: $architecture")
                        entry?.name?.let { println("Entry: ${entry!!.name}") }
                        if (entry?.name?.startsWith(os) == true && entry?.name?.contains(architecture, true) == true) {
                            file.outputStream().use { output ->
                                stream.copyTo(output)
                            }
                            break
                        }
                        stream.closeEntry()
                    }
                    stream.close()
                    logger.info("Discord JNI downloaded successfully.")
                }
            }
        }
    }

    private fun load(sdkFile: File, jniFile: File) {
        if (SystemUtils.IS_OS_WINDOWS) System.load(sdkFile.absolutePath)
        System.load(jniFile.absolutePath)
        Core.initDiscordNative(sdkFile.absolutePath)
        successful = true
    }

    private fun determineSdkFileName() =
        if (SystemUtils.IS_OS_WINDOWS) "discord_game_sdk.dll"
        else if (SystemUtils.IS_OS_MAC) "libdiscord_game_sdk.dylib"
        else if (SystemUtils.IS_OS_LINUX) "libdiscord_game_sdk.so"
        else null
    private fun determineJniFileName() =
        if (SystemUtils.IS_OS_WINDOWS) "discord_game_sdk_jni.dll"
        else if (SystemUtils.IS_OS_MAC) "libdiscord_game_sdk_jni.dylib"
        else if (SystemUtils.IS_OS_LINUX) "libdiscord_game_sdk_jni.so"
        else null
}
