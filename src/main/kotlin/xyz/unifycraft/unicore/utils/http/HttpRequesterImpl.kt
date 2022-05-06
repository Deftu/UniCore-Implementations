package xyz.unifycraft.unicore.utils.http

import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.utils.http.HttpRequester
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import xyz.unifycraft.unicore.api.utils.http.SSLBuilder
import java.security.cert.CertificateFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class HttpRequesterImpl : HttpRequester {
    override lateinit var httpClient: OkHttpClient

    fun initialize() {
        httpClient = OkHttpClient.Builder()
            .addInterceptor {
                it.proceed(it.request().newBuilder()
                    .header("User-Agent", "Mozilla 4.76 (${UniCore.getName()}/${UniCore.getVersion()})").build())
            }.build() // TODO: Use our custom SSL context so we can use updated certs.
    }

    override fun <T> request(request: Request, block: (Response) -> T): T {
        val response = httpClient.newCall(request).execute()
        val value = block.invoke(response)
        response.close()
        return value
    }

    companion object {
        val sslContext = SSLBuilder()
            .load("isrgrootx1", "assets/unicore/certs/isrgrootx1.der")
            .load("lets-encrypt-r3", "assets/unicore/certs/lets-encrypt-r3.der")
            .build()
        val sslSocketFactory
            get() = sslContext.socketFactory
    }
}