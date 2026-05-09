import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
    id("com.gradleup.shadow") version("9.3.0")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.groupez.dev/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly(files("libraries/ByteClans-paper-1.7.0.jar"))
    compileOnly("fr.maxlego08.menu:zmenu-api:1.1.0.0")
    compileOnly("me.clip:placeholderapi:2.12.2")

    compileOnly("org.incendo:cloud-paper:2.0.0-beta.10")
    compileOnly("org.incendo:cloud-annotations:2.0.0")

}

paper {
    name = getProjectName(rootProject.name)
    main = "${rootProject.group}.${rootProject.name.replace("-", "")}.PaperPlugin"
    description = rootProject.description
    version = rootProject.version.toString()
    apiVersion = "1.19"

    authors = listOf("Bytephoria", "iAmForyy_")
    website = "https://bytephoria.team"
    generateLibrariesJson = true
    foliaSupported = true

    serverDependencies {
        register("PlaceholderAPI") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("ByteClans") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("zMenu") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

    }

}

tasks {
    generatePaperPluginDescription {
        useGoogleMavenCentralProxy()
    }

    shadowJar {
        exclude(
            "org/h2/**",
            "org/slf4j/**",
            "org/spongepowered/**",
            "com/zaxxer/**",
            "com/github/benmanes/caffeine/**",
            "net/kyori/**",
            "com/google/errorprone/**",
            "io/leangen/geantyref/**",
            "org/jspecify/annotations/**"
        )

        archiveBaseName.set(getProjectName(rootProject.name))
        archiveVersion.set(rootProject.version.toString())
        archiveClassifier.set("")

    }

}

/**
 * Converts a hyphen-separated project name into PascalCase.
 */
fun getProjectName(baseName: String): String {
    return baseName.split("-")
        .joinToString("") {
                part -> part.replaceFirstChar {
            it.uppercase()
        }
        }
}