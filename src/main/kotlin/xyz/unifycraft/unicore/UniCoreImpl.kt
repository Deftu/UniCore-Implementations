package xyz.unifycraft.unicore

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gg.essential.elementa.utils.ResourceCache
import gg.essential.universal.UMinecraft
import me.kbrewster.eventbus.*
import xyz.deftu.deftils.Multithreading
import xyz.deftu.quicksocket.common.utils.QuickSocketJsonHandler
import xyz.unifycraft.unicore.api.UniCore
import xyz.unifycraft.unicore.api.UniCoreConfig
import xyz.unifycraft.unicore.api.commands.CommandRegistry
import xyz.unifycraft.unicore.api.events.InitializationEvent
import xyz.unifycraft.unicore.api.gui.ElementaHud
import xyz.unifycraft.unicore.api.gui.notifications.Notifications
import xyz.unifycraft.unicore.api.keybinds.KeyBindRegistry
import xyz.unifycraft.unicore.api.utils.*
import xyz.unifycraft.unicore.api.utils.http.HttpRequester
import xyz.unifycraft.unicore.api.utils.hypixel.HypixelHelper
import xyz.unifycraft.unicore.api.utils.updater.Updater
import xyz.unifycraft.unicore.cloud.CloudConnection
import xyz.unifycraft.unicore.cloud.CloudJsonParser
import xyz.unifycraft.unicore.commands.CommandRegistryImpl
import xyz.unifycraft.unicore.commands.UniCoreCommand
import xyz.unifycraft.unicore.keybinds.KeyBindRegistryImpl
import xyz.unifycraft.unicore.utils.http.HttpRequesterImpl
import xyz.unifycraft.unicore.utils.hypixel.HypixelHelperImpl
import xyz.unifycraft.unicore.utils.updater.UpdaterEventListener
import java.util.*

//#if MC<=11202
import net.minecraftforge.common.MinecraftForge
import xyz.unifycraft.unicore.api.gui.hud.HudRegistry
import xyz.unifycraft.unicore.api.utils.deleter.Deleter
import xyz.unifycraft.unicore.features.ModWarning
import xyz.unifycraft.unicore.gui.ElementaHudImpl
import xyz.unifycraft.unicore.gui.hud.HudRegistryImpl
import xyz.unifycraft.unicore.gui.hud.TestHudElement
import xyz.unifycraft.unicore.gui.notifications.NotificationsImpl
import xyz.unifycraft.unicore.onboarding.Onboarding
import xyz.unifycraft.unicore.onboarding.OnboardingEventListener
import xyz.unifycraft.unicore.utils.*
import xyz.unifycraft.unicore.utils.deleter.DeleterImpl
import xyz.unifycraft.unicore.utils.updater.UpdaterImpl
import net.minecraftforge.fml.common.Mod as ForgeMod

@ForgeMod(
    name = "UniCore",
    version = "__VERSION__",
    modid = "unicore",
    clientSideOnly = true
)
//#endif
class UniCoreImpl : UniCore {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    private val eventBus = eventbus {  }

    // API
    private lateinit var fileHelper: FileHelper
    private lateinit var config: UniCoreConfig
    private lateinit var jsonHelper: JsonHelper
    private lateinit var guiHelper: GuiHelper
    private lateinit var modLoaderHelper: ModLoaderHelper
    private lateinit var elementaResourceCache: ResourceCache
    private lateinit var elementaHud: ElementaHud
    private lateinit var notifications: Notifications
    private lateinit var commandRegistry: CommandRegistry
    private lateinit var keyBindRegistry: KeyBindRegistry
    private lateinit var httpRequester: HttpRequester
    private lateinit var hudRegistry: HudRegistry
    private lateinit var deleter: Deleter
    private lateinit var updater: Updater
    private lateinit var mojangHelper: MojangHelper
    private lateinit var hypixelHelper: HypixelHelper
    private lateinit var internetHelper: InternetHelper
    private lateinit var colorHelper: ColorHelper

    // Implementation
    private lateinit var cloudConnection: CloudConnection

    override fun initialize(event: InitializationEvent) {
        // APIs
        //#if MC<=11202
        listOf(
            UpdaterEventListener()
        ).forEach(MinecraftForge.EVENT_BUS::register)
        //#endif

        fileHelper = FileHelperImpl(event.gameDir)
        config = UniCoreConfig().also { it.initialize() }
        jsonHelper = JsonHelperImpl()
        guiHelper = GuiHelperImpl()
        modLoaderHelper = ModLoaderHelperImpl()
        elementaResourceCache = ResourceCache()
        elementaHud = ElementaHudImpl()
        notifications = NotificationsImpl()
        commandRegistry = CommandRegistryImpl().also { it.registerCommand(UniCoreCommand()) }
        keyBindRegistry = KeyBindRegistryImpl(fileHelper.dataDir)
        httpRequester = HttpRequesterImpl().also {  it.initialize() }
        hudRegistry = HudRegistryImpl()
        deleter = DeleterImpl().also { it.initialize() }
        updater = UpdaterImpl()
        mojangHelper = MojangHelperImpl()
        hypixelHelper = HypixelHelperImpl()
        internetHelper = InternetHelperImpl()
        colorHelper = ColorHelperImpl()

        // Features
        //ModWarning.initialize()
        Onboarding.initialize()
        eventBus.register(OnboardingEventListener())
        QuickSocketJsonHandler.applyJsonParser(CloudJsonParser())
        cloudConnection = CloudConnection(
            sessionId = UUID.randomUUID(),
            headers = arrayOf(
                "uuid" to UMinecraft.getMinecraft().session.profile.id.toString()
            )
        ).apply {
            Multithreading.runAsync {
                if (!Onboarding.isToS()) return@runAsync
                tryConnect()
            }
        }
    }

    override fun withInstance(instance: UniCore) {
        UniCoreImpl.instance = instance as UniCoreImpl
    }

    override fun gson(): Gson = gson
    override fun eventBus() = eventBus

    override fun fileHelper() = fileHelper
    override fun config() = config
    override fun jsonHelper() = jsonHelper
    override fun guiHelper() = guiHelper
    override fun modLoaderHelper() = modLoaderHelper
    override fun elementaResourceCache() = elementaResourceCache
    override fun elementaHud() = elementaHud
    override fun notifications() = notifications
    override fun commandRegistry() = commandRegistry
    override fun keyBindRegistry() = keyBindRegistry
    override fun httpRequester() = httpRequester
    override fun hudRegistry() = hudRegistry
    override fun deleter() = deleter
    override fun updater() = updater
    override fun mojangHelper() = mojangHelper
    override fun hypixelHelper() = hypixelHelper
    override fun internetHelper() = internetHelper
    override fun colorHelper() = colorHelper

    fun cloudConnection() = cloudConnection

    companion object {
        lateinit var instance: UniCoreImpl
            private set
    }
}