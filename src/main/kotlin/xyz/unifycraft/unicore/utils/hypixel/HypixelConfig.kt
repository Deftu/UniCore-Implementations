package xyz.unifycraft.unicore.utils.hypixel

import xyz.unifycraft.configured.ConfigHolder
import xyz.unifycraft.configured.data.Option
import xyz.unifycraft.configured.data.type.switch
import xyz.unifycraft.configured.data.type.text

class HypixelConfig : ConfigHolder {
    override val options = mutableListOf<Option>()

    var saveHypixelApiKeys by switch(true) {
        name = "Save Hypixel API keys"
        description = "Save your Hypixel API keys in the config file. This will allow us to use the Hypixel API without you having to re-input it every time it's needed."
    }
    var apiKey by text {
        name = "Hypixel API key"
        description = "The API key needed to make requests for Hypixel data."
        protected = true
        limit = 50
    }
}