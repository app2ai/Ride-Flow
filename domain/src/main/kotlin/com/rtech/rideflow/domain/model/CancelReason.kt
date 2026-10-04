package com.rtech.rideflow.domain.model

/** Why a ride ended in [RideState.Cancelled]. */
enum class CancelReason {
    /** The rider cancelled. */
    RIDER_CANCELLED,

    /** The driver cancelled after accepting. */
    DRIVER_CANCELLED,

    /** The driver didn't reach pickup within the no-show window. */
    DRIVER_NO_SHOW,

    /** No driver accepted the request. */
    NO_DRIVERS_FOUND
}
