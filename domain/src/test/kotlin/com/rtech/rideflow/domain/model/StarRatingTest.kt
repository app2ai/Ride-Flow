package com.rtech.rideflow.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StarRatingTest {
    @Test
    fun `given the minimum stars, when creating a rating, then it is created`() {
        assertEquals(StarRating.MIN, StarRating.of(StarRating.MIN)?.value)
    }

    @Test
    fun `given the maximum stars, when creating a rating, then it is created`() {
        assertEquals(StarRating.MAX, StarRating.of(StarRating.MAX)?.value)
    }

    @Test
    fun `given stars below the minimum, when creating a rating, then it returns null`() {
        assertNull(StarRating.of(StarRating.MIN - 1))
    }

    @Test
    fun `given stars above the maximum, when creating a rating, then it returns null`() {
        assertNull(StarRating.of(StarRating.MAX + 1))
    }
}
