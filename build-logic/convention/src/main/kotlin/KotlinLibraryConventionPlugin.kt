import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Convention plugin `rideflow.kotlin.library`, applied to `:domain`.
 *
 * Applies the Kotlin JVM plugin (no Android SDK on the classpath), sets the shared Java/JVM
 * target, and adds coroutines plus the standard unit-test stack (JUnit4, MockK, Turbine,
 * coroutines-test).
 */
class KotlinLibraryConventionPlugin : Plugin<Project> {

    /**
     * Applies the shared pure-Kotlin library configuration to [target].
     *
     * @param target the module the plugin is applied to.
     */
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("kotlin-jvm"))
            optInToExperimentalTime()

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JAVA_VERSION
                targetCompatibility = JAVA_VERSION
            }

            extensions.configure<KotlinJvmProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JVM_TARGET)
                }
            }

            dependencies {
                add("implementation", libs.library("kotlinx-coroutines-core"))

                add("testImplementation", libs.library("junit"))
                add("testImplementation", libs.library("mockk"))
                add("testImplementation", libs.library("turbine"))
                add("testImplementation", libs.library("kotlinx-coroutines-test"))
            }
        }
    }

    private companion object {
        val JAVA_VERSION = JavaVersion.VERSION_11
        val JVM_TARGET = JvmTarget.JVM_11
    }
}
