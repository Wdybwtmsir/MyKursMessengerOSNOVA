pluginManagement {
    repositories {
        // Официальное независимое зеркало создателей Kotlin, работающее без блокировок TLS
        maven { url = java.net.URI("https://jetbrains.com") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyKursMessenger"
include(":app")
