package xyz.unifycraft.unicore.utils.updater

import gg.essential.universal.UDesktop
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.utils.updater.UpdateFetcher
import xyz.unifycraft.unicore.api.utils.updater.Updater
import xyz.unifycraft.unicore.api.utils.updater.UpdaterMod
import xyz.unifycraft.unicore.gui.updater.UpdaterScreen
import xyz.unifycraft.unicore.utils.updater.fetchers.GitHubUpdateFetcher
import xyz.unifycraft.unicore.utils.updater.fetchers.JsonUpdateFetcher
import java.io.File

class UpdaterImpl : Updater {
    override val mods = mutableListOf<UpdaterMod>()
    override var outdated = listOf<UpdaterMod>()

    override fun include(name: String, version: String, id: String, path: String, fetcher: UpdateFetcher, file: File) {
        mods.add(UpdaterMod(name, version, id, path, fetcher, file))
    }
    override fun includeJson(
        name: String,
        version: String,
        id: String,
        file: File,
        url: String,
        versionFieldName: String,
        checksumFieldName: String?
    ) = include(name, version, id, url, JsonUpdateFetcher(versionFieldName, checksumFieldName), file)
    override fun includeGitHub(name: String, version: String, id: String, file: File, repository: String) = include(name, version, id, "https://api.github.com/repos/${repository}/releases/latest", GitHubUpdateFetcher(), file)

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun check() {
        if (UniCore.getConfig().updateCheck) {
            val outdated = mutableListOf<UpdaterMod>()
            for (mod in mods) {
                GlobalScope.launch(Dispatchers.IO) {
                    mod.fetcher.check(this@UpdaterImpl, mod)
                    if (mod.fetcher.hasUpdate()) outdated.add(mod).also {
                        this@UpdaterImpl.outdated = outdated
                    }
                }
            }

            if (!registeredShutdownHook) {
                Runtime.getRuntime().addShutdownHook(Thread({
                    var changes = false
                    val arguments = mutableListOf<File>()
                    for (mod in outdated) {
                        if (mod.allowedUpdate) {
                            try {
                                if (System.getProperty("os.name").lowercase().contains("mac")) {
                                    val sipStatus = Runtime.getRuntime().exec("csrutil status")
                                    sipStatus.waitFor()
                                    if (!sipStatus.inputStream.use { it.bufferedReader().readText() }
                                            .contains("System Integrity Protection status: disabled.")) {
                                        UDesktop.open(mod.file.parentFile)
                                    }
                                }

                                arguments.add(mod.file)
                                changes = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    if (changes) UniCore.getDeleter().delete(arguments)
                }, "${UniCore.getName()} Updater Deletion Thread"))
                registeredShutdownHook = true
            }

            if (outdated.isNotEmpty()) {
                UniCore.getNotifications().post(
                    title = UniCore.getName(),
                    description = "Some of your mods are outdated! Click me to download updates.",
                    click = {
                        UniCore.getGuiHelper().showScreen(UpdaterScreen())
                    })
            }
        }
    }

    companion object {
        private var registeredShutdownHook = false
    }
}
