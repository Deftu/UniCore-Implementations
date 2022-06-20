package xyz.unifycraft.unicore.features

import com.google.gson.JsonObject
import me.kbrewster.eventbus.Subscribe
import net.minecraftforge.fml.common.Loader
import okhttp3.Request
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.events.PostInitializationEvent

object ModWarning {
    const val URL = "..."
    private lateinit var mods: List<Mod>

    fun initialize() {
        UniCore.getEventBus().register(this)
        val json = UniCore.getHttpRequester().request(Request.Builder()
            .url(URL)
            .get()
            .build()) {
            val string = it.body?.string() ?: return@request JsonObject()
            UniCore.getJsonHelper().parse(string).asJsonObject ?: JsonObject()
        }
        mods = UniCore.getJsonHelper().gson.fromJson(json, Array<Mod>::class.java).toList()
    }

    @Subscribe
    fun onPostInitialize(event: PostInitializationEvent) {
        val mods = mods.filter(Mod::isLoaded)
        if (mods.isEmpty()) return
        for (mod in mods) {
            UniCore.getNotifications().post("Warning - ${mod.name}", mod.reason)
        }
    }
}

internal data class Mod(
    val name: String,
    val id: String,
    val reason: String
) {
    fun isLoaded() = UniCore.getModLoaderHelper().isModLoaded(id)
}
