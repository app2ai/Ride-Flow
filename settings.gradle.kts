pluginManagement {
    includeBuild("build-logic")
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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "RideFlow"
include(":app")
include(":domain")
include(":data")
include(":core:network")
include(":core:location")
include(":core:designsystem")
include(":core:navigation")
include(":core:common")
include(":feature:onboarding")
include(":feature:home")
include(":feature:ride-booking")
include(":feature:live-tracking")
include(":feature:driver-mode")
include(":feature:payment")
include(":feature:ride-history")
include(":feature:profile")
