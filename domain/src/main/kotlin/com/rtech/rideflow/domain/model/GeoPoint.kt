package com.rtech.rideflow.domain.model

/**
 * A point on the earth's surface, independent of any maps SDK.
 *
 * @property latitude latitude in decimal degrees, `-90.0..90.0`.
 * @property longitude longitude in decimal degrees, `-180.0..180.0`.
 */
data class GeoPoint(val latitude: Double, val longitude: Double)
