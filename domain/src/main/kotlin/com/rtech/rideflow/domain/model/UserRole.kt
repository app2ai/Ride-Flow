package com.rtech.rideflow.domain.model

/** Which side of the marketplace a [User] is on. */
enum class UserRole {
    /** Books rides. */
    RIDER,

    /** Drives and accepts ride offers. */
    DRIVER
}
