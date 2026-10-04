import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

/**
 * The `libs` version catalog (`gradle/libs.versions.toml`), for use inside convention plugins.
 */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Looks up a library alias in the `libs` catalog, failing the build if it is missing.
 *
 * @param alias the catalog alias, e.g. `"kotlinx-coroutines-core"`.
 * @return a provider of the resolved dependency.
 */
internal fun VersionCatalog.library(alias: String) = findLibrary(alias).get()

/**
 * Looks up a plugin ID in the `libs` catalog, failing the build if it is missing.
 *
 * @param alias the catalog plugin alias, e.g. `"kotlin-compose"`.
 * @return the plugin ID.
 */
internal fun VersionCatalog.pluginId(alias: String): String = findPlugin(alias).get().get().pluginId

/**
 * Opts every Kotlin compilation in this module into `kotlin.time.ExperimentalTime`.
 *
 * `kotlin.time.Instant` (which replaced `kotlinx.datetime.Instant` in kotlinx-datetime 0.7+) is
 * still experimental in Kotlin 2.2, and it appears in public `:domain` entities.
 */
internal fun Project.optInToExperimentalTime() {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    }
}
