import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin `rideflow.android.library`, applied to `:data` and every `:core:*` module.
 *
 * Applies the Android library plugin (with AGP's built-in Kotlin support) and sets the
 * SDK levels, Java target and test runner shared by all RideFlow Android libraries.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {

    /**
     * Applies the shared Android library configuration to [target].
     *
     * @param target the module the plugin is applied to.
     */
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                compileSdk = COMPILE_SDK

                defaultConfig {
                    minSdk = MIN_SDK
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JAVA_VERSION
                    targetCompatibility = JAVA_VERSION
                }
            }
        }
    }

    private companion object {
        const val COMPILE_SDK = 37
        const val MIN_SDK = 29
        val JAVA_VERSION = JavaVersion.VERSION_11
    }
}
