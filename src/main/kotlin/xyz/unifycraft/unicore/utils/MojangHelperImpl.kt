package xyz.unifycraft.unicore.utils

import okhttp3.Request
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.utils.MojangHelper
import java.time.OffsetDateTime
import java.util.*

class MojangHelperImpl : MojangHelper {
    override fun toUuid(username: String, timestamp: Long): UUID? {
        val request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/$username?at=$timestamp")
            .get()
            .build()
        return UniCore.getHttpRequester().request(request) {
            it.body?.let { body ->
                val raw = UniCore.getJsonHelper().parse(body.string())
                if (!raw.isJsonObject) return@let null
                val json = raw.asJsonObject
                return@let UUID.fromString(json.get("id").asJsonPrimitive.asString)
            }
        }
    }

    override fun toUuid(username: String) = toUuid(username, OffsetDateTime.now().toEpochSecond())

    override fun fromUuid(uuid: UUID): String? {
        val request = Request.Builder()
            .url("https://sessionserver.mojang.com/session/minecraft/profile/$uuid")
            .get()
            .build()
        return UniCore.getHttpRequester().request(request) {
            it.body?.let { body ->
                val raw = UniCore.getJsonHelper().parse(body.string())
                if (!raw.isJsonObject) return@let null
                val json = raw.asJsonObject
                return@let json.get("name").asJsonPrimitive.asString
            }
        }
    }
}
