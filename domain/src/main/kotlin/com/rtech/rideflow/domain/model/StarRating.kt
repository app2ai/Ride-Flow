package com.rtech.rideflow.domain.model

/**
 * A star rating from [MIN] to [MAX] inclusive. Build one with [of]; an out-of-range value can't
 * be represented.
 *
 * @property value number of stars.
 */
@JvmInline
value class StarRating private constructor(val value: Int) {
    /** Bounds and factory for [StarRating]. */
    companion object {
        /** Lowest allowed rating. */
        const val MIN: Int = 1

        /** Highest allowed rating. */
        const val MAX: Int = 5

        /**
         * Creates a rating if [stars] is in range.
         *
         * @param stars number of stars.
         * @return the rating, or `null` if [stars] is outside [MIN]..[MAX].
         */
        fun of(stars: Int): StarRating? = if (stars in MIN..MAX) StarRating(stars) else null
    }
}
