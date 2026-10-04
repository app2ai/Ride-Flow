package com.rtech.rideflow.domain.model

import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Full state × event matrix: every pair that isn't a valid transition must return the current
 * state unchanged (the same instance).
 */
@RunWith(Parameterized::class)
class RideStateMachineInvalidTransitionTest(private val state: RideState, private val event: RideEvent) {
    @Test
    fun `given a state, when an invalid event arrives, then the state is unchanged`() {
        assertSame(state, RideStateMachine.transition(state, event))
    }

    /** Builds the invalid (state, event) pairs. */
    companion object {
        private val validPairs: Set<Pair<Class<*>, Class<*>>> = setOf(
            RideState.Idle::class.java to RideEvent.SearchRide::class.java,
            RideState.Searching::class.java to RideEvent.DriverFound::class.java,
            RideState.DriverAssigned::class.java to RideEvent.DriverEnRoute::class.java,
            RideState.DriverArriving::class.java to RideEvent.RideStarted::class.java,
            RideState.InProgress::class.java to RideEvent.RideCompleted::class.java,
            RideState.PaymentPending::class.java to RideEvent.PaymentDone::class.java,
            RideState.Searching::class.java to RideEvent.Cancel::class.java,
            RideState.DriverAssigned::class.java to RideEvent.Cancel::class.java,
            RideState.DriverArriving::class.java to RideEvent.Cancel::class.java
        )

        /**
         * Every (state, event) pair not in the valid-transition table.
         *
         * @return the parameter rows, one `[state, event]` array each.
         */
        @JvmStatic
        @Parameterized.Parameters(name = "given {0}, when {1}, then unchanged")
        fun invalidPairs(): List<Array<Any>> = RideStateFixtures.allStates.flatMap { state ->
            RideStateFixtures.allEvents
                .filterNot { event -> (state.javaClass to event.javaClass) in validPairs }
                .map { event -> arrayOf<Any>(state, event) }
        }
    }
}
