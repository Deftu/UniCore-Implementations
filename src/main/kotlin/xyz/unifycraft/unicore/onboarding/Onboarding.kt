package xyz.unifycraft.unicore.onboarding

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import xyz.deftu.quicksocket.common.utils.QuickSocketJsonHandler
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.gui.onboarding.OnboardingScreen
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
            data = UniCore.getJsonHelper().gson.fromJson(file.readText(), OnboardingData::class.java)
        }
    }

    fun save() {
        if (this::data.isInitialized) {
            file.writeText(UniCore.getJsonHelper().gson.toJson(data))
        }
    }

    fun openScreen() {
        UniCore.getGuiHelper().showScreen(OnboardingScreen())
    }

    fun isOnboardingSeen() = data.seenOnboarding
    fun setOnboardingSeen(seen: Boolean) {
        data.seenOnboarding = seen
        save()
    }

    fun isToS() = data.tos
    fun setToS(tos: Boolean) = apply {
        data.tos = tos
        save()
    }
}

data class OnboardingData(
    @SerializedName("seen_onboarding")
    var seenOnboarding: Boolean = false,
    @SerializedName("tos")
    var tos: Boolean = false
)
