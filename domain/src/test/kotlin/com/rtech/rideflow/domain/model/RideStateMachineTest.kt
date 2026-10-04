package com.rtech.rideflow.domain.model

import com.rtech.rideflow.domain.model.RideStateFixtures.RIDE_ID
import com.rtech.rideflow.domain.model.RideStateFixtures.driver
import com.rtech.rideflow.domain.model.RideStateFixtures.eta
import com.rtech.rideflow.domain.model.RideStateFixtures.fare
import com.rtech.rideflow.domain.model.RideStateFixtures.startedAt
import org.junit.Assert.assertEquals
import org.junit.Test

/** The valid transitions; [RideStateMachineInvalidTransitionTest] covers every other pair. */
class RideStateMachineTest {
    @Test
    fun `given Idle, when SearchRide, then Searching`() {
        assertEquals(
            RideState.Searching,
            RideStateMachine.transition(RideState.Idle, RideEvent.SearchRide)
        )
    }

    @Test
    fun `given Searching, when DriverFound, then DriverAssigned with that driver`() {
        assertEquals(
            RideState.DriverAssigned(driver),
            RideStateMachine.transition(RideState.Searching, RideEvent.DriverFound(driver))
        )
    }

    @Test
    fun `given DriverAssigned, when DriverEnRoute, then DriverArriving keeps the driver and sets the eta`() {
        assertEquals(
            RideState.DriverArriving(driver, eta),
            RideStateMachine.transition(RideState.DriverAssigned(driver), RideEvent.DriverEnRoute(eta))
        )
    }

    @Test
    fun `given DriverArriving, when RideStarted, then InProgress with the event's id and start time`() {
        assertEquals(
            RideState.InProgress(RIDE_ID, startedAt),
            RideStateMachine.transition(
                RideState.DriverArriving(driver, eta),
                RideEvent.RideStarted(RIDE_ID, startedAt)
            )
        )
    }

    @Test
    fun `given InProgress, when RideCompleted, then PaymentPending with the final fare`() {
        assertEquals(
            RideState.PaymentPending(fare),
            RideStateMachine.transition(RideState.InProgress(RIDE_ID, startedAt), RideEvent.RideCompleted(fare))
        )
    }

    @Test
    fun `given PaymentPending, when PaymentDone, then Done`() {
        assertEquals(
            RideState.Done,
            RideStateMachine.transition(RideState.PaymentPending(fare), RideEvent.PaymentDone)
        )
    }

    @Test
    fun `given Searching, when Cancel, then Cancelled with the reason`() {
        assertEquals(
            RideState.Cancelled(CancelReason.NO_DRIVERS_FOUND),
            RideStateMachine.transition(RideState.Searching, RideEvent.Cancel(CancelReason.NO_DRIVERS_FOUND))
        )
    }

    @Test
    fun `given DriverAssigned, when Cancel, then Cancelled with the reason`() {
        assertEquals(
            RideState.Cancelled(CancelReason.DRIVER_CANCELLED),
            RideStateMachine.transition(
                RideState.DriverAssigned(driver),
                RideEvent.Cancel(CancelReason.DRIVER_CANCELLED)
            )
        )
    }

    @Test
    fun `given DriverArriving, when Cancel, then Cancelled with the reason`() {
        assertEquals(
            RideState.Cancelled(CancelReason.DRIVER_NO_SHOW),
            RideStateMachine.transition(
                RideState.DriverArriving(driver, eta),
                RideEvent.Cancel(CancelReason.DRIVER_NO_SHOW)
            )
        )
    }
}
