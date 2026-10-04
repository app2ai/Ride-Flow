package com.rtech.rideflow.domain.model

/**
 * A payment for a ride.
 *
 * @property id unique payment id.
 * @property rideId id of the [Ride] being paid for.
 * @property amountPaise amount charged, in the [currency]'s minor unit (paise for INR).
 * @property method how the rider pays.
 * @property status processing status.
 * @property currency currency of [amountPaise].
 * @property transactionId gateway transaction id, or `null` until the gateway assigns one
 *   (always `null` for [PaymentMethod.CASH]).
 */
data class Payment(
    val id: String,
    val rideId: String,
    val amountPaise: Long,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val currency: Currency = Currency.INR,
    val transactionId: String? = null
)
