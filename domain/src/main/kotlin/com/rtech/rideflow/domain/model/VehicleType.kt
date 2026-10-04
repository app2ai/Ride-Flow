package com.rtech.rideflow.domain.model

/** Ride category a rider can book; selects the fare strategy. */
enum class VehicleType {
    /** Three-wheeler auto-rickshaw. */
    AUTO,

    /** Compact hatchback. */
    MINI,

    /** Standard sedan. */
    SEDAN,

    /** Six- or seven-seater SUV. */
    SUV,

    /** Pooled ride shared with other riders on a similar route. */
    SHARED
}
