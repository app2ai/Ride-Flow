import dev.detekt.gradle.extensions.DetektExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

// Static analysis for every module: `./gradlew ktlintCheck detekt`.
subprojects {
    pluginManager.apply(rootProject.libs.plugins.ktlint.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.detekt.get().pluginId)

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig.set(true)
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    }
}
