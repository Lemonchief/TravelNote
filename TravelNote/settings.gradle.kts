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
        maven("https://artifactory-external.vkpartner.ru/artifactory/maven/")
        maven("https://artifactory.yandex.net/artifactory/maven/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://artifactory-external.vkpartner.ru/artifactory/maven/")
        maven("https://artifactory.yandex.net/artifactory/maven/")
    }
}

rootProject.name = "Travel Note"
include(":app")
