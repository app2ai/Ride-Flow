package com.rtech.rideflow.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A single GPS fix, as published by a driver or read from the device.
 *
 * @property point where the device was.
 * @property timestamp when the fix was taken.
 * @property bearing direction of travel in degrees clockwise from north, or `null` if unknown.
 * @property speedMetersPerSecond ground speed in m/s, or `null` if unknown.
 * @property accuracyMeters horizontal accuracy radius in metres, or `null` if unknown.
 */
data class Location @OptIn(ExperimentalTime::class) constructor(
    val point: GeoPoint,
    val timestamp: Instant,
    val bearing: Float? = null,
    val speedMetersPerSecond: Float? = null,
    val accuracyMeters: Float? = null
)
