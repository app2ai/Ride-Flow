package com.rtech.rideflow.domain.model

/** How a rider pays for a ride. */
enum class PaymentMethod {
    /** Cash handed to the driver. */
    CASH,

    /** Credit or debit card. */
    CARD,

    /** UPI. */
    UPI,

    /** Prepaid wallet. */
    WALLET
}
