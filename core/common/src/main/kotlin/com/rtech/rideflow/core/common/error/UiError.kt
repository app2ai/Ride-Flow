package com.rtech.rideflow.core.common.error

import androidx.annotation.StringRes
import com.rtech.rideflow.core.common.R

/**
 * An error ready to show to the user, carried on MVI `State` or an error `Effect`.
 *
 * ViewModels map sealed domain errors to a [UiError]; Screens resolve [messageRes] with
 * `stringResource`. Never carry a raw message `String` instead.
 */
sealed interface UiError {
    /** String resource with the user-facing message. */
    @get:StringRes
    val messageRes: Int

    /** The device has no network connection. */
    data object NoConnection : UiError {
        override val messageRes: Int = R.string.core_common_error_no_connection
    }

    /** The server didn't respond in time. */
    data object Timeout : UiError {
        override val messageRes: Int = R.string.core_common_error_timeout
    }

    /** The session expired or was revoked; the user must sign in again. */
    data object SessionExpired : UiError {
        override val messageRes: Int = R.string.core_common_error_session_expired
    }

    /** The server failed to handle the request. */
    data object Server : UiError {
        override val messageRes: Int = R.string.core_common_error_server
    }

    /** Anything not covered by a more specific error. */
    data object Unknown : UiError {
        override val messageRes: Int = R.string.core_common_error_unknown
    }

    /**
     * A feature-specific error whose message lives in the feature's own resources, e.g. a
     * ride-request validation failure.
     *
     * @property messageRes string resource with the user-facing message.
     */
    data class Custom(@StringRes override val messageRes: Int) : UiError
}
