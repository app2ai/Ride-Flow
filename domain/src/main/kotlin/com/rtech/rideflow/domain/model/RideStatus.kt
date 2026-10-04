package com.rtech.rideflow.domain.model

/**
 * Lifecycle status stored on a [Ride].
 *
 * Status changes only through the ride lifecycle transitions defined in `domain/CLAUDE.md`, never
 * by copying a [Ride] with an arbitrary new status.
 */
enum class RideStatus {
    /** Looking for a driver. */
    SEARCHING,

    /** A driver has accepted. */
    DRIVER_ASSIGNED,

    /** The driver is on the way to pickup. */
    DRIVER_ARRIVING,

    /** The rider is on board. */
    IN_PROGRESS,

    /** The trip is over and payment hasn't completed. */
    PAYMENT_PENDING,

    /** Trip and payment are complete. */
    DONE,

    /** Cancelled before the trip started. */
    CANCELLED
}
