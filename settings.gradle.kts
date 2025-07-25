// File: settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // ✅ --- هذا هو السطر المهم في مكانه الصحيح ---
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "DaawahTV"
include(":app")