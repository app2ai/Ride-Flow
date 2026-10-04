package com.rtech.rideflow.domain.model

/**
 * A rating one participant of a ride gives the other (rider to driver, or driver to rider).
 *
 * @property id unique review id.
 * @property rideId id of the [Ride] being reviewed.
 * @property fromUserId id of the [User] who wrote the review.
 * @property toUserId id of the [User] being reviewed.
 * @property rating stars given.
 * @property comment optional free-text comment, or `null` if none.
 */
data class Review(
    val id: String,
    val rideId: String,
    val fromUserId: String,
    val toUserId: String,
    val rating: StarRating,
    val comment: String? = null
)
