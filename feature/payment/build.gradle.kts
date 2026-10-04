plugins {
    alias(libs.plugins.rideflow.android.feature)
}

android {
    namespace = "com.rtech.rideflow.feature.payment"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
}
