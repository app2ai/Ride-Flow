package com.rtech.rideflow.domain.model

/** Where a [Payment] is in processing. */
enum class PaymentStatus {
    /** Created; not yet sent to the gateway. */
    PENDING,

    /** Charged successfully. */
    SUCCEEDED,

    /** The charge failed; can be retried. */
    FAILED,

    /** The charge was refunded. */
    REFUNDED
}
