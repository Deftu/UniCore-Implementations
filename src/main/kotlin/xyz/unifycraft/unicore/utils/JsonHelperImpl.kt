package xyz.unifycraft.unicore.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import xyz.unifycraft.unicore.api.utils.JsonHelper

class JsonHelperImpl : JsonHelper {
    override val gson = GsonBuilder()
        .setPrettyPrinting()
        //#if MC>=11400
        //$$ .setLenient()
        //#endif
        .create()
    //#if MC<=11202
    override val jsonParser = JsonParser()
    //#endif

    override fun parse(json: String): JsonElement {
        //#if MC<=11202
        return jsonParser.parse(json)
        //#else
        //$$ JsonParser.parseString(json)
        //#endif
    }
}
