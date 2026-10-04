package com.rtech.rideflow.domain.model

/**
 * Itemised price of a ride. All amounts are in the [currency]'s minor unit (paise for INR).
 *
 * The fare strategy that builds a fare owns the arithmetic, including rounding; this class only
 * carries the result.
 *
 * @property baseFarePaise flat charge for starting the ride.
 * @property distanceFarePaise charge for distance travelled.
 * @property timeFarePaise charge for time spent, including waiting.
 * @property taxPaise tax on the fare.
 * @property totalPaise amount the rider pays.
 * @property surgeMultiplierPercent surge applied, in percent: [NO_SURGE_PERCENT] means no surge,
 *   `150` means 1.5×.
 * @property currency currency of every amount above.
 */
data class Fare(
    val baseFarePaise: Long,
    val distanceFarePaise: Long,
    val timeFarePaise: Long,
    val taxPaise: Long,
    val totalPaise: Long,
    val surgeMultiplierPercent: Int = NO_SURGE_PERCENT,
    val currency: Currency = Currency.INR
) {
    /** Constants for [Fare]. */
    companion object {
        /** [surgeMultiplierPercent] value meaning no surge (1.0×). */
        const val NO_SURGE_PERCENT: Int = 100
    }
}
