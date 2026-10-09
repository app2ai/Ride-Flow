package com.rtech.rideflow.core.common.error

import com.rtech.rideflow.core.common.R
import org.junit.Assert.assertEquals
import org.junit.Test

class UiErrorTest {
    @Test
    fun `given the built-in errors, when reading their messages, then each has its own string resource`() {
        val builtIn = listOf(
            UiError.NoConnection,
            UiError.Timeout,
            UiError.SessionExpired,
            UiError.Server,
            UiError.Unknown
        )

        assertEquals(builtIn.size, builtIn.map { it.messageRes }.toSet().size)
    }

    @Test
    fun `given a custom error, when reading its message, then it is the resource it was built with`() {
        val messageRes = R.string.core_common_error_unknown

        assertEquals(messageRes, UiError.Custom(messageRes).messageRes)
    }
}
