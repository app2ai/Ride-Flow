package com.rtech.rideflow.domain.model

import kotlin.time.Duration

/**
 * A driving route between pickup and drop.
 *
 * @property pickup start point.
 * @property drop end point.
 * @property polyline encoded polyline for drawing the route on a map.
 * @property distanceMeters total driving distance in metres.
 * @property estimatedDuration expected driving time.
 * @property waypoints intermediate stops, in order; empty for a direct trip.
 */
data class Route(
    val pickup: GeoPoint,
    val drop: GeoPoint,
    val polyline: String,
    val distanceMeters: Long,
    val estimatedDuration: Duration,
    val waypoints: List<GeoPoint> = emptyList()
)
