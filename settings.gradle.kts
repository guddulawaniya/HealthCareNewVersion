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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "HealthCareNewVersion"
include(":app")
include(":core-model")
include(":core-network")
include(":core-utils")
include(":core-ui")
include(":core-service")
include(":dashboardModule")
include(":login-module")
include(":community-module")
include(":Upscale-module")
include(":service-module")
include(":core-navigation")
include(":CallModule")
include(":alzimer-module")
include(":Medical-reminder")
include(":dietition-module")
