plugins {
    kotlin("jvm") version("1.6.10") apply(false)
    id("xyz.unifycraft.gradle.multiversion-root") version("1.6.0-beta.6")
}

preprocess {
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")
}
