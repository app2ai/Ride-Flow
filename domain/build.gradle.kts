plugins {
    alias(libs.plugins.rideflow.kotlin.library)
}

dependencies {
    // kotlinx.datetime.Instant appears in public domain entities, so consumers need it too.
    api(libs.kotlinx.datetime)
}
