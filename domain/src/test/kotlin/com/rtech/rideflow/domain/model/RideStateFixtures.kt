package com.rtech.rideflow.domain.model

import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Shared sample values for [RideStateMachine] tests. */
internal object RideStateFixtures {
    private const val VEHICLE_YEAR = 2023
    private const val FARE_BASE_PAISE = 5_000L
    private const val FARE_DISTANCE_PAISE = 12_000L
    private const val FARE_TIME_PAISE = 3_000L
    private const val FARE_TAX_PAISE = 1_000L
    private const val FARE_TOTAL_PAISE = 21_000L
    private const val STARTED_AT_EPOCH_SECONDS = 1_760_000_000L
    private const val ETA_MINUTES = 4

    val driver = Driver(
        id = "driver-1",
        userId = "user-2",
        vehicle = Vehicle(
            id = "vehicle-1",
            make = "Maruti Suzuki",
            model = "Dzire",
            year = VEHICLE_YEAR,
            plateNo = "KA01AB1234",
            type = VehicleType.SEDAN
        ),
        licenseNo = "KA0120230001234",
        status = DriverStatus.BUSY
    )

    val fare = Fare(
        baseFarePaise = FARE_BASE_PAISE,
        distanceFarePaise = FARE_DISTANCE_PAISE,
        timeFarePaise = FARE_TIME_PAISE,
        taxPaise = FARE_TAX_PAISE,
        totalPaise = FARE_TOTAL_PAISE
    )

    val eta = ETA_MINUTES.minutes
    @OptIn(ExperimentalTime::class)
    val startedAt: Instant = Instant.fromEpochSeconds(STARTED_AT_EPOCH_SECONDS)
    const val RIDE_ID = "ride-1"

    /** One instance of every [RideState]. */
    @OptIn(ExperimentalTime::class)
    val allStates: List<RideState> = listOf(
        RideState.Idle,
        RideState.Searching,
        RideState.DriverAssigned(driver),
        RideState.DriverArriving(driver, eta),
        RideState.InProgress(RIDE_ID, startedAt),
        RideState.PaymentPending(fare),
        RideState.Done,
        RideState.Cancelled(CancelReason.RIDER_CANCELLED)
    )

    /** One instance of every [RideEvent]. */
    @OptIn(ExperimentalTime::class)
    val allEvents: List<RideEvent> = listOf(
        RideEvent.SearchRide,
        RideEvent.DriverFound(driver),
        RideEvent.DriverEnRoute(eta),
        RideEvent.RideStarted(RIDE_ID, startedAt),
        RideEvent.RideCompleted(fare),
        RideEvent.PaymentDone,
        RideEvent.Cancel(CancelReason.RIDER_CANCELLED)
    )
}
