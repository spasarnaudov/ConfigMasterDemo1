import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import java.io.FileInputStream
import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

val localProperties = Properties().apply {
    val localPropsFile = File(rootDir, "local.properties") // rootDir instead of rootProject
    if (localPropsFile.exists()) {
        load(FileInputStream(localPropsFile))
    } else {
        println("local.properties not found in root directory!")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/spasarnaudov/ConfigMaster")
            credentials {
                username = localProperties.getProperty("gpr.user") ?: ""
                password = localProperties.getProperty("gpr.key") ?: ""
            }
        }

        mavenCentral()
    }
}

rootProject.name = "ConfigMasterDemo1"
include(":app")