package com.rtech.rideflow.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A ride booked by a rider.
 *
 * @property id unique ride id.
 * @property riderId id of the rider's [User].
 * @property status current lifecycle status.
 * @property pickup where the rider is picked up.
 * @property drop where the rider is dropped.
 * @property route planned route from [pickup] to [drop].
 * @property fare quoted fare, or the final fare once the ride completes.
 * @property createdAt when the ride was requested.
 * @property driverId id of the assigned [Driver], or `null` while [RideStatus.SEARCHING] or if
 *   cancelled before assignment.
 */
data class Ride @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val riderId: String,
    val status: RideStatus,
    val pickup: GeoPoint,
    val drop: GeoPoint,
    val route: Route,
    val fare: Fare,
    val createdAt: Instant,
    val driverId: String? = null
)
