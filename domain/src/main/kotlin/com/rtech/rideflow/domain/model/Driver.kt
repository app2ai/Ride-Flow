package com.rtech.rideflow.domain.model

/**
 * A driver's working profile. Personal details live on the linked [User].
 *
 * @property id unique driver id.
 * @property userId id of the [User] account this driver belongs to.
 * @property vehicle the vehicle the driver is currently using.
 * @property licenseNo driving licence number.
 * @property status current availability.
 * @property currentLocation last known location, or `null` if none has been published.
 */
data class Driver(
    val id: String,
    val userId: String,
    val vehicle: Vehicle,
    val licenseNo: String,
    val status: DriverStatus,
    val currentLocation: Location? = null
)
