package xyz.unifycraft.unicore.gui.hud

import com.google.gson.JsonObject
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.gui.hud.HudElement
import java.io.File

class HudRegistryConfig {
    private var initialized = false
    lateinit var file: File
        private set
    lateinit var config: JsonObject
        private set

    fun initialize() {
        if (initialized) return

        // Set up our file
        file = File(UniCore.getFileHelper().dataDir, "hud.json")
        if (!file.exists()) {
            file.createNewFile()
            config = JsonObject()
            save()
        }

        // Read our file to JSON.
        config = UniCore.getJsonHelper().parse(file.readText()).asJsonObject

        initialized = true
    }

    fun save() {
        file.writeText(UniCore.getGson().toJson(config))
    }

    fun load(element: HudElement): HudConfig {
        if (!initialized) throw IllegalStateException("HudRegistryConfig is not initialized!")
        val elementsArray = config["elements"].asJsonArray
        val elementConfig = elementsArray.firstOrNull {
            it.asJsonObject["name"].asString == element.metadata.getName().lowercase()
        }?.asJsonObject
        if (elementConfig == null) {
            val newConfig = UniCore.getGson().toJsonTree(HudConfig(5.0, 5.0)).asJsonObject
            config["elements"].asJsonArray.add(newConfig)
            save()
            return load(element)
        } else return UniCore.getGson().fromJson(elementConfig, HudConfig::class.java)
    }
}

data class HudConfig(
    val x: Double,
    val y: Double
)
