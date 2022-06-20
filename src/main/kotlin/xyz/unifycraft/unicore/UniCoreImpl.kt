package xyz.unifycraft.unicore

import gg.essential.elementa.utils.ResourceCache
import gg.essential.universal.UMinecraft
import me.kbrewster.eventbus.*
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
import xyz.deftu.deftils.Multithreader
import xyz.unifycraft.unicore.api.utils.deleter.Deleter
import xyz.unifycraft.unicore.gui.ElementaHudImpl
import xyz.unifycraft.unicore.gui.notifications.NotificationsImpl
import xyz.unifycraft.unicore.onboarding.Onboarding
import xyz.unifycraft.unicore.onboarding.OnboardingEventListener
import xyz.unifycraft.unicore.utils.*
import xyz.unifycraft.unicore.utils.deleter.DeleterImpl
import xyz.unifycraft.unicore.utils.updater.UpdaterImpl
import xyz.unifycraft.unicore.rpc.DiscordHandler
import java.util.*
import java.util.concurrent.TimeUnit

//#if MC<=11202
import net.minecraftforge.common.MinecraftForge
import xyz.unifycraft.unicore.api.gui.ComponentFactory
import xyz.unifycraft.unicore.api.utils.text.TextHelper
import xyz.unifycraft.unicore.utils.text.TextHelperImpl
import net.minecraftforge.fml.common.Mod as ForgeMod

@ForgeMod(
    name = "UniCore",
    version = "__VERSION__",
    modid = "unicore",
    clientSideOnly = true
)
//#endif
class UniCoreImpl : UniCore {
    private val eventBus = eventbus {  }
    private val multithreader = Multithreader(75)

    // API
    private lateinit var fileHelper: FileHelper
    private lateinit var config: UniCoreConfig
    private lateinit var componentFactory: ComponentFactory
    private lateinit var jsonHelper: JsonHelper
    private lateinit var guiHelper: GuiHelper
    private lateinit var textHelper: TextHelper
    private lateinit var chatHelper: ChatHelper
    private lateinit var modLoaderHelper: ModLoaderHelper
    private lateinit var elementaResourceCache: ResourceCache
    private lateinit var elementaHud: ElementaHud
    private lateinit var messageQueue: MessageQueue
    private lateinit var notifications: Notifications
    private lateinit var commandRegistry: CommandRegistry
    private lateinit var keyBindRegistry: KeyBindRegistry
    private lateinit var httpRequester: HttpRequester
    private lateinit var deleter: Deleter
    private lateinit var updater: Updater
    private lateinit var mojangHelper: MojangHelper
    private lateinit var hypixelHelper: HypixelHelper
    private lateinit var colorHelper: ColorHelper

    // Implementation
    private lateinit var discordHandler: DiscordHandler
    private lateinit var cloudConnection: CloudConnection

    override fun initialize(event: InitializationEvent) {
        eventBus.register(UniCoreImpl)

        // APIs
        //#if MC<=11202
        listOf(
            EventExtender(),
            UpdaterEventListener()
        ).forEach(MinecraftForge.EVENT_BUS::register)
        //#endif

        fileHelper = FileHelperImpl(event.gameDir)
        config = UniCoreConfig().also { it.initialize() }
        //componentFactory = ComponentFactoryImpl()
        jsonHelper = JsonHelperImpl()
        guiHelper = GuiHelperImpl()
        textHelper = TextHelperImpl()
        chatHelper = ChatHelperImpl().also { it.initialize() }
        modLoaderHelper = ModLoaderHelperImpl()
        elementaResourceCache = ResourceCache()
        elementaHud = ElementaHudImpl().also { it.initialize() }
        messageQueue = MessageQueueImpl().also { it.initialize() }
        notifications = NotificationsImpl()
        commandRegistry = CommandRegistryImpl().also { it.registerCommand(UniCoreCommand()) }
        keyBindRegistry = KeyBindRegistryImpl(fileHelper.dataDir)
        httpRequester = HttpRequesterImpl().also {  it.initialize() }
        deleter = DeleterImpl().also { it.initialize() }
        updater = UpdaterImpl()
        mojangHelper = MojangHelperImpl()
        hypixelHelper = HypixelHelperImpl().also { it.initialize() }
        colorHelper = ColorHelperImpl()

        // Features
        //ModWarning.initialize()
        Onboarding.initialize()
        eventBus.register(OnboardingEventListener())
        QuickSocketJsonHandler.applyJsonParser(CloudJsonParser())
        discordHandler = DiscordHandler().also { it.initialize() }
        cloudConnection = CloudConnection(
            sessionId = UUID.randomUUID(),
            headers = arrayOf(
                "uuid" to UMinecraft.getMinecraft().session.profile.id.toString()
            )
        ).apply {
            multithreader.runAsync {
                if (!Onboarding.isToS()) return@runAsync
                tryConnect()
            }
        }

        textHelper.create("Hello, World! <red>Thanks!</red> [Here's a link for you...](https://example.com/)")
    }

    override fun withInstance(instance: UniCore) {
        UniCoreImpl.instance = instance as UniCoreImpl
    }

    override fun eventBus() = eventBus
    override fun multithreader() = multithreader

    override fun fileHelper() = fileHelper
    override fun config() = config
    override fun componentFactory() = componentFactory
    override fun jsonHelper() = jsonHelper
    override fun guiHelper() = guiHelper
    override fun textHelper() = textHelper
    override fun chatHelper() = chatHelper
    override fun modLoaderHelper() = modLoaderHelper
    override fun elementaResourceCache() = elementaResourceCache
    override fun elementaHud() = elementaHud
    override fun messageQueue() = messageQueue
    override fun notifications() = notifications
    override fun commandRegistry() = commandRegistry
    override fun keyBindRegistry() = keyBindRegistry
    override fun httpRequester() = httpRequester
    override fun deleter() = deleter
    override fun updater() = updater
    override fun mojangHelper() = mojangHelper
    override fun hypixelHelper() = hypixelHelper
    override fun colorHelper() = colorHelper

    fun cloudConnection() = cloudConnection

    companion object {
        lateinit var instance: UniCoreImpl
            private set
    }
}