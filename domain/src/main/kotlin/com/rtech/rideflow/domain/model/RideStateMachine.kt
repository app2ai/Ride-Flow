package com.rtech.rideflow.domain.model

/**
 * The only place ride lifecycle transitions are defined.
 *
 * Valid transitions:
 *
 * | From | Event | To |
 * |---|---|---|
 * | [RideState.Idle] | [RideEvent.SearchRide] | [RideState.Searching] |
 * | [RideState.Searching] | [RideEvent.DriverFound] | [RideState.DriverAssigned] |
 * | [RideState.DriverAssigned] | [RideEvent.DriverEnRoute] | [RideState.DriverArriving] |
 * | [RideState.DriverArriving] | [RideEvent.RideStarted] | [RideState.InProgress] |
 * | [RideState.InProgress] | [RideEvent.RideCompleted] | [RideState.PaymentPending] |
 * | [RideState.PaymentPending] | [RideEvent.PaymentDone] | [RideState.Done] |
 * | Searching, DriverAssigned, DriverArriving | [RideEvent.Cancel] | [RideState.Cancelled] |
 *
 * Any other (state, event) pair is invalid and returns the current state unchanged.
 */
object RideStateMachine {
    /**
     * Applies [event] to [state]. Pure: no I/O, no clock, never throws.
     *
     * @param state the current state.
     * @param event what happened.
     * @return the next state, or [state] itself if [event] isn't valid from [state].
     */
    fun transition(state: RideState, event: RideEvent): RideState = when (event) {
        RideEvent.SearchRide -> onSearchRide(state)
        is RideEvent.DriverFound -> onDriverFound(state, event)
        is RideEvent.DriverEnRoute -> onDriverEnRoute(state, event)
        is RideEvent.RideStarted -> onRideStarted(state, event)
        is RideEvent.RideCompleted -> onRideCompleted(state, event)
        RideEvent.PaymentDone -> onPaymentDone(state)
        is RideEvent.Cancel -> onCancel(state, event)
    }

    private fun onSearchRide(state: RideState): RideState = if (state is RideState.Idle) RideState.Searching else state

    private fun onDriverFound(state: RideState, event: RideEvent.DriverFound): RideState =
        if (state is RideState.Searching) RideState.DriverAssigned(event.driver) else state

    private fun onDriverEnRoute(state: RideState, event: RideEvent.DriverEnRoute): RideState =
        if (state is RideState.DriverAssigned) RideState.DriverArriving(state.driver, event.eta) else state

    private fun onRideStarted(state: RideState, event: RideEvent.RideStarted): RideState =
        if (state is RideState.DriverArriving) RideState.InProgress(event.rideId, event.startedAt) else state

    private fun onRideCompleted(state: RideState, event: RideEvent.RideCompleted): RideState =
        if (state is RideState.InProgress) RideState.PaymentPending(event.fare) else state

    private fun onPaymentDone(state: RideState): RideState =
        if (state is RideState.PaymentPending) RideState.Done else state

    private fun onCancel(state: RideState, event: RideEvent.Cancel): RideState =
        if (state.isCancellable()) RideState.Cancelled(event.reason) else state

    private fun RideState.isCancellable(): Boolean = when (this) {
        RideState.Searching,
        is RideState.DriverAssigned,
        is RideState.DriverArriving
        -> true

        RideState.Idle,
        is RideState.InProgress,
        is RideState.PaymentPending,
        RideState.Done,
        is RideState.Cancelled
        -> false
    }
}
