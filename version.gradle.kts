import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.unifycraft.gradle.ModLoader
import xyz.unifycraft.gradle.utils.GameSide

plugins {
    kotlin("jvm")
    java
    id("xyz.unifycraft.gradle.multiversion")
    id("xyz.unifycraft.gradle.tools")
    id("xyz.unifycraft.gradle.tools.shadow")
    id("xyz.unifycraft.gradle.tools.blossom")
}

base.archivesName.set("${modData.name}-${mcData.versionStr}-${mcData.loader.name}".toLowerCase())
loom.mixin.defaultRefmapName.set("mixins.${modData.id}.refmap.json")

loomHelper {
    useTweaker("xyz.unifycraft.unicore.api.mixins.UniCoreDevTweaker")
    useProperty("mixin.debug", "true", GameSide.CLIENT)
    disableRunConfigs(GameSide.SERVER)
    //if (mcData.loader == ModLoader.forge)
    //    loom.useForgeMixin(projectId)
}

val dummy by sourceSets.creating

blossom {
    replaceToken("__VERSION__", project.version)
}

repositories {
    maven("https://repo.hypixel.net/repository/Hypixel/")
}

dependencies {
    // Implement API
    val unicoreApi = "xyz.unifycraft.unicore.api:unicore-${mcData.versionStr}-${mcData.loader.name}:${modData.version}"
    unishade(unicoreApi)
    "dummyCompileOnly"(unicoreApi)

    // Independent of API
    unishade("com.github.JnCrMx:discord-game-sdk4j:v0.5.5")

    // Ducks
    implementation(dummy.output)
}

tasks {
    named<Jar>("jar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    named<ShadowJar>("unishadowJar") {
        relocate("com.google.gson", "${modData.group}.lib.gson")
    }
}
