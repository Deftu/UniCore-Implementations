package xyz.unifycraft.unicore.cloud

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.logging.log4j.LogManager
import xyz.deftu.quicksocket.common.utils.buildJsonObject
import xyz.unifycraft.unicore.api.UniCore
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Arrays

object AccountValidator {
    private const val URL = "https://sessionserver.mojang.com/session/minecraft/join"
    private val constant = BigInteger("173be201d4e5591dcef37bcaf701d136", 16).toByteArray()
    private val random = SecureRandom()

    fun validate(token: String, uuid: String): Boolean {
        return UniCore.getHttpRequester().request(
            Request.Builder()
                .post(createRequestBody(token, uuid, generateHash(generateSecretKey())))
                .url(URL)
                .build()
        ) {
            it.code == 204
        }
    }

    private fun createRequestBody(token: String, uuid: String, hash: String) = buildJsonObject {
        addProperty("accessToken", token)
        addProperty("selectedProfile", uuid)
        addProperty("serverId", hash)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())

    private fun generateHash(secretKey: ByteArray): String {
        if (secretKey.size != 16)
            throw IllegalArgumentException("Secret key must be 16 bytes long")
        val buffer = Arrays.copyOf(secretKey, secretKey.size + constant.size)
        System.arraycopy(constant, 0, buffer, secretKey.size, constant.size)
        return BigInteger(MessageDigest.getInstance("SHA-1").digest(buffer)).toString(16)
    }

    private fun generateSecretKey(): ByteArray {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes
    }
}