package com.rtech.rideflow.domain.model

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Something that happened to a ride, fed to [RideStateMachine.transition].
 *
 * Events carry every timestamp and id the next state needs; the state machine never reads a
 * clock or generates ids.
 */
sealed interface RideEvent {
    /** The rider requested a ride. */
    data object SearchRide : RideEvent

    /**
     * A driver accepted the request.
     *
     * @property driver the driver who accepted.
     */
    data class DriverFound(val driver: Driver) : RideEvent

    /**
     * The assigned driver started heading to pickup.
     *
     * @property eta estimated time until the driver reaches pickup.
     */
    data class DriverEnRoute(val eta: Duration) : RideEvent

    /**
     * The rider was picked up.
     *
     * @property rideId id of the ride.
     * @property startedAt when the trip started.
     */
    data class RideStarted @OptIn(ExperimentalTime::class) constructor(val rideId: String, val startedAt: Instant) : RideEvent

    /**
     * The rider was dropped off.
     *
     * @property fare final fare to be paid.
     */
    data class RideCompleted(val fare: Fare) : RideEvent

    /** Payment for the trip succeeded. */
    data object PaymentDone : RideEvent

    /**
     * The ride was cancelled.
     *
     * @property reason why it was cancelled.
     */
    data class Cancel(val reason: CancelReason) : RideEvent
}
