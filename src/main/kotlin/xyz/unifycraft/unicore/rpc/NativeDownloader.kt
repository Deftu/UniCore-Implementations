package xyz.unifycraft.unicore.rpc

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

    val sdkUrl = "https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip"
    val jniUrl = "https://raw.githubusercontent.com/UnifyCraft/PublicData/main/discord_jni.zip"

    fun download(directory: File) {
        if (!directory.exists() && !directory.mkdirs()) throw IllegalStateException("Could not create directory ${directory.absolutePath}")
        val sdkFile = File(directory, determineSdkFileName())
        val jniFile = File(directory, determineJniFileName())
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
                    val architecture = System.getProperty("os.arch").lowercase().let { if (it == "amd64") "x86_64" else it }
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
        /*UniCore.getHttpRequester().request(Request.Builder()
            .get()
            .url(jniUrl)
            .build()) {
            if (!it.isSuccessful) throw IllegalStateException("Failed to download JNI")
            it.body?.byteStream()?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                    unzip(file)
                    logger.info("Discord JNI downloaded successfully.")
                }
            }
        }*/
    }

    private fun load(sdkFile: File, jniFile: File) {
        if (SystemUtils.IS_OS_WINDOWS) System.load(sdkFile.absolutePath)
        System.load(jniFile.absolutePath)
        Core.initDiscordNative(sdkFile.absolutePath)
    }

    private fun determineSdkFileName() =
        if (SystemUtils.IS_OS_WINDOWS) "discord_game_sdk.dll"
        else if (SystemUtils.IS_OS_MAC) "libdiscord_game_sdk.dylib"
        else if (SystemUtils.IS_OS_LINUX) "libdiscord_game_sdk.so"
        else throw IllegalStateException("Unsupported operating system! (${SystemUtils.OS_NAME})")
    private fun determineJniFileName() =
        if (SystemUtils.IS_OS_WINDOWS) "discord_game_sdk_jni.dll"
        else if (SystemUtils.IS_OS_MAC) "libdiscord_game_sdk_jni.dylib"
        else if (SystemUtils.IS_OS_LINUX) "libdiscord_game_sdk_jni.so"
        else throw IllegalStateException("Unsupported operating system! (${SystemUtils.OS_NAME})")
}
