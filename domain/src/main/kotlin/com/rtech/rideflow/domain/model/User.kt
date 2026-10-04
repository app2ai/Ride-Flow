package com.rtech.rideflow.domain.model

/**
 * A registered RideFlow account, rider or driver.
 *
 * @property id unique user id.
 * @property name display name.
 * @property phone verified phone number in E.164 format, e.g. `+919876543210`.
 * @property role whether the user rides or drives.
 * @property email email address, or `null` if the user hasn't added one.
 * @property rating average star rating from past reviews, or `null` if not yet rated.
 * @property profilePhotoUrl URL of the profile photo, or `null` if none is set.
 */
data class User(
    val id: String,
    val name: String,
    val phone: String,
    val role: UserRole,
    val email: String? = null,
    val rating: Double? = null,
    val profilePhotoUrl: String? = null
)
