package xyz.unifycraft.unicore

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import xyz.deftu.quicksocket.common.utils.QuickSocketJsonHandler
import xyz.unifycraft.unicore.api.UniCore
import java.io.File

object Onboarding {
    private lateinit var file: File
    private lateinit var data: OnboardingData

    fun initialize() {
        file = File(UniCore.getFileHelper().dataDir, "onboarding.json")
        if (!file.exists()) {
            file.createNewFile()
            data = OnboardingData()
            save()
        } else {
            data = UniCore.getGson().fromJson(file.readText(), OnboardingData::class.java)
        }
    }

    fun save() {
        if (this::data.isInitialized) {
            file.writeText(UniCore.getGson().toJson(data))
        }
    }

    fun isToS() = data.tos
    fun setToS(tos: Boolean) = apply {
        data.tos = tos
        save()
    }

    fun isCrashReporting() = data.crashReporting
    fun setCrashReporting(crashReporting: Boolean) = apply {
        data.crashReporting = crashReporting
        save()
    }
}

class OnboardingData {
    @SerializedName("tos")
    var tos = false
    @SerializedName("crash_tracking")
    var crashReporting = false
}
