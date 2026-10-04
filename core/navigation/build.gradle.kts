plugins {
    alias(libs.plugins.rideflow.android.library)
}

android {
    namespace = "com.rtech.rideflow.core.navigation"
}

dependencies {
    implementation(project(":core:common"))

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}
