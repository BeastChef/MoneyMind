pluginManagement {

    repositories {

// Порядок важен: сначала google(), потом остальное

        google() // обязательно для плагина google-services

        gradlePluginPortal()

        mavenCentral()

        maven("https://jitpack.io")

    }


}

dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {

        google()

        mavenCentral()

        maven("https://jitpack.io")

    }

}

rootProject.name = "MoneyMind"

include(":app")
