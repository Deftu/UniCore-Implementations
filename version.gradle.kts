import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.unifycraft.gradle.ModLoader
import xyz.unifycraft.gradle.utils.GameSide
import xyz.unifycraft.gradle.utils.disableRunConfigs
import xyz.unifycraft.gradle.utils.useForgeMixin
import xyz.unifycraft.gradle.utils.useProperty
import xyz.unifycraft.gradle.utils.useMinecraftTweaker

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("xyz.unifycraft.gradle.multiversion")
    id("gg.essential.loom")
    id("xyz.unifycraft.gradle.tools")
    id("xyz.unifycraft.gradle.snippets.shadow")
    id("net.kyori.blossom") version("1.3.0")
    id("java")
}

val projectVersion: String by project
version = projectVersion
val projectGroup: String by project
group = projectGroup
val projectId: String by project

useMinecraftTweaker("xyz.unifycraft.unicore.api.mixins.UniCoreDevTweaker")
loom.useProperty("mixin.debug", "true", GameSide.CLIENT)
loom.disableRunConfigs(GameSide.SERVER)
if (mcData.loader == ModLoader.forge)
    loom.useForgeMixin(projectId)

val dummy by sourceSets.creating

blossom {
    replaceToken("__VERSION__", project.version)
}

dependencies {
    // Implement API
    val unicoreApi = "xyz.unifycraft.unicore:unicore-${mcData.versionStr}-${mcData.loader.name}:${project.version}"
    unishade(unicoreApi)
    "dummyCompileOnly"(unicoreApi)

    // Ducks
    implementation(dummy.output)
}

tasks {
    named<Jar>("jar") {
        val projectName: String by project
        archiveBaseName.set("$projectName-${mcData.versionStr}-${mcData.loader.name}".toLowerCase())
    }

    named<ShadowJar>("unishadowJar") {
        val projectGroup: String by project
        relocate("com.google.gson", "${projectGroup}.lib.gson")
    }

    processResources {
        val projectId: String by project

        filesMatching(listOf("mods.toml", "fabric.mod.json", "mcmod.info")) {
            expand(mapOf(
                "id" to projectId,
                "version" to project.version,
                "name" to project.name,
                "mcversion" to mcData.versionStr
            ))
        }

        filesMatching("mixins.${projectId}.json") {
            expand(mapOf(
                "javaversion" to if (mcData.javaVersion.isJava8) "JAVA_8" else if (mcData.javaVersion.isCompatibleWith(JavaVersion.VERSION_16)) "JAVA_16" else "JAVA_17"
            ))
        }
    }
}
