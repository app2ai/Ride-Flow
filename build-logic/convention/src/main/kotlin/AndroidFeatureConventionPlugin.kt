import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin `rideflow.android.feature`, applied to every `:feature:*` module.
 *
 * Builds on `rideflow.android.library`, enables Jetpack Compose, and adds everything an MVI
 * feature needs: `:domain`, Compose (via the BOM), lifecycle-aware state collection, Koin,
 * coroutines, and the unit-test and Compose UI-test stacks.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {

    /**
     * Applies the shared feature-module configuration to [target].
     *
     * @param target the module the plugin is applied to.
     */
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("rideflow-android-library"))
            pluginManager.apply(libs.pluginId("kotlin-compose"))

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                val composeBom = platform(libs.library("androidx-compose-bom"))

                add("implementation", project(":domain"))

                add("implementation", composeBom)
                add("implementation", libs.library("androidx-compose-ui"))
                add("implementation", libs.library("androidx-compose-ui-graphics"))
                add("implementation", libs.library("androidx-compose-ui-tooling-preview"))
                add("implementation", libs.library("androidx-compose-material3"))
                add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel-compose"))
                add("implementation", libs.library("koin-android"))
                add("implementation", libs.library("koin-androidx-compose"))
                add("implementation", libs.library("kotlinx-coroutines-core"))

                add("debugImplementation", libs.library("androidx-compose-ui-tooling"))
                add("debugImplementation", libs.library("androidx-compose-ui-test-manifest"))

                add("testImplementation", libs.library("junit"))
                add("testImplementation", libs.library("mockk"))
                add("testImplementation", libs.library("turbine"))
                add("testImplementation", libs.library("kotlinx-coroutines-test"))

                add("androidTestImplementation", composeBom)
                add("androidTestImplementation", libs.library("androidx-compose-ui-test-junit4"))
                add("androidTestImplementation", libs.library("androidx-junit"))
                add("androidTestImplementation", libs.library("androidx-espresso-core"))
            }
        }
    }
}
