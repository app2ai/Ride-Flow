package com.rtech.rideflow.domain.model

/** A driver's availability for new ride offers. */
enum class DriverStatus {
    /** Available and receiving ride offers. */
    ONLINE,

    /** On a ride; not receiving new offers. */
    BUSY,

    /** Signed off; not receiving offers or publishing location. */
    OFFLINE
}
