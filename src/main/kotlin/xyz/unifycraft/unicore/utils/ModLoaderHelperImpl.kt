package xyz.unifycraft.unicore.utils

import xyz.unifycraft.unicore.api.utils.ModLoaderHelper

//#if MC<=11202
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.common.Loader
//#elseif MC>=11404
//$$ import org.apache.maven.artifact.versioning.DefaultArtifactVersion
//$$ import net.minecraftforge.fml.loading.FMLEnvironment
//$$ import net.minecraftforge.fml.ModList
//#elseif FABRIC==1
//$$ import net.fabricmc.loader.api.FabricLoader
//#endif

class ModLoaderHelperImpl : ModLoaderHelper {
    override fun isModLoaded(id: String): Boolean {
        //#if MC<=11202
        return Loader.isModLoaded(id)
        //#elseif MC>=11404 && FORGE==1
        //$$ return ModList.get().isLoaded(id)
        //#elseif FABRIC==1
        //$$ return FabricLoader.getInstance().isModLoaded(id)
        //#endif
    }

    override fun isModLoaded(id: String, version: String): Boolean {
        return isModLoaded(id) &&
                //#if MC<=11202
                (Loader.instance().modList.firstOrNull {
                    it.modId == id
                }?.version == version)
                //#elseif MC>=11404 && FORGE==1
                //$$ ModList.get().getModContainerById(id).get()?.modInfo?.version?.compareTo(DefaultArtifactVersion(version)) == 0
                //#elseif FABRIC==1
                //$$ (FabricLoader.getInstance().getModContainer(id)?.metadata?.version?.friendlyString == version) ?: false
                //#endif
    }

    override fun isDevelopmentEnvironment(): Boolean {
        //#if MC<=11202
        val value = Launch.blackboard["fml.deobfuscatedEnvironment"] ?: return false
        return value as Boolean
        //#elseif MC>=11404
        //$$ FMLEnvironment.production
        //#elseif FABRIC==1
        //$$ return FabricLoader.getInstance().isDevelopmentEnvironment()
        //#endif
    }
}
