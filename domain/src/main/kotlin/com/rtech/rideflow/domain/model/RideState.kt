package com.rtech.rideflow.domain.model

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Where the current ride is in its lifecycle, from the rider's point of view.
 *
 * Move between states only with [RideStateMachine.transition]; never construct the next state
 * directly.
 */
sealed interface RideState {
    /** No ride in progress. */
    data object Idle : RideState

    /** A ride request is out and the system is looking for a driver. */
    data object Searching : RideState

    /**
     * A driver accepted the request.
     *
     * @property driver the assigned driver.
     */
    data class DriverAssigned(val driver: Driver) : RideState

    /**
     * The assigned driver is heading to pickup.
     *
     * @property driver the assigned driver.
     * @property eta estimated time until the driver reaches pickup.
     */
    data class DriverArriving(val driver: Driver, val eta: Duration) : RideState

    /**
     * The rider is on board.
     *
     * @property rideId id of the ride.
     * @property startedAt when the trip started.
     */
    data class InProgress @OptIn(ExperimentalTime::class) constructor(val rideId: String, val startedAt: Instant) : RideState

    /**
     * The trip is over and is waiting for payment.
     *
     * @property fare final fare to be paid.
     */
    data class PaymentPending(val fare: Fare) : RideState

    /** The trip and its payment are complete. Terminal. */
    data object Done : RideState

    /**
     * The ride was cancelled before the trip started. Terminal.
     *
     * @property reason why it was cancelled.
     */
    data class Cancelled(val reason: CancelReason) : RideState
}
