# :domain — Module Rules

This module is **pure Kotlin**. It is the core of RideFlow and must stay independent
of Android, frameworks, and every other module. These rules add to the root `CLAUDE.md`.

## Allowed

- Kotlin standard library
- `kotlinx-coroutines-core` (Flow, suspend)
- `kotlin.time.Instant` and `kotlin.time.Duration` for timestamps and durations
  (`kotlinx.datetime.Instant` was removed in kotlinx-datetime 0.7+); `kotlinx-datetime` itself
  is exposed as `api` from `domain/build.gradle.kts` for calendar/time-zone work
- `javax.inject`-free plain classes (Koin wiring happens in `:app`, not here)

## Forbidden

- Any `android.*` or `androidx.*` import
- `Context`, `ViewModel`, `Parcelable`, `@Parcelize`
- Room, Retrofit, OkHttp, Gson/Moshi/kotlinx-serialization annotations
- Koin imports
- Dependencies on `:data`, `:core:*`, `:feature:*`, `:app`

If a task seems to require any of the above here, the code belongs in another module.
Stop and say where it should go instead.

## What Lives Here

```
domain/src/main/kotlin/com/rtech/rideflow/domain/
  model/          Entities: User, Driver, Vehicle, Ride, Location, Fare, Route, Payment, Review
                  Value types: GeoPoint, StarRating, Currency, VehicleType, UserRole, DriverStatus,
                  RideStatus, PaymentMethod, PaymentStatus, RideState, CancelReason
                  RideRequest + RideRequest.Builder (Builder pattern)
                  RideTimings (business timing constants, see below)
  repository/     Interfaces only: RideRepository, LocationSubscriber, LocationPublisher,
                  FareRepository, AuthRepository, PaymentRepository
  usecase/        One class per operation, grouped by area (ride/, fare/, location/, auth/, payment/)
                  BaseRideUseCase (Template Method) lives here
  fare/           FareStrategy interface, strategy implementations, FareStrategyFactory
  validation/     RideRequestValidator chain + the small interfaces validators depend on
  error/          Domain error types (sealed)
```

The Phase 4 table in the HLD/LLD lists the Builder and validation chain under
`:feature:ride-booking`. That's the module that *uses* them; the code lives here.

## Rules

- Entities are immutable `data class`es. No mutable `var` properties.
- `RideState` is a `sealed interface`. Every transition is defined in one place.
- Use cases expose a single `operator fun invoke(...)` and depend only on repository interfaces.
- Return `Result<T>` or `Flow<Result<T>>`. Represent failures with sealed domain errors, not raw exceptions.
- Inject a `CoroutineDispatcher` when a use case needs one; never reference `Dispatchers.*` directly.
- Money is never `Double` in new code paths: use `Long` paise (minor units) for fare totals.
- Every class here must be testable with plain JUnit4 + MockK, with no Robolectric and no emulator.
- Repository functions that return a `Flow` are **not** `suspend`. Only one-shot calls are `suspend`.
- `SurgeFareStrategy` is a Decorator: it takes the wrapped `FareStrategy` in its constructor.
- Validators never reach into other modules. Each depends on a small domain interface
  (e.g. `AuthSession`, `AreaServiceability`, `DriverAvailability`) that `:data` implements,
  and fails with a sealed `RideRequestError`, never a thrown or wrapped exception.

## Ride Lifecycle (`RideState`)

`RideState` is a `sealed interface` with: `Idle`, `Searching`, `DriverAssigned(driver)`,
`DriverArriving(driver, eta)`, `InProgress(rideId, startedAt)`, `PaymentPending(fare)`,
`Done`, `Cancelled(reason)`. These are the only valid transitions; anything else returns the
current state unchanged:

| From | Event | To |
|---|---|---|
| `Idle` | search ride | `Searching` |
| `Searching` | driver found | `DriverAssigned` |
| `DriverAssigned` | driver en route | `DriverArriving` |
| `DriverArriving` | ride started | `InProgress` |
| `InProgress` | ride completed | `PaymentPending` |
| `PaymentPending` | payment done | `Done` |
| `Searching`, `DriverAssigned`, `DriverArriving` | cancel | `Cancelled` |

`startedAt` and any other timestamps arrive on the event; the transition function never reads
a clock.

## Business Timings

Named constants in `model/RideTimings.kt`, never inline literals:

| Constant | Value | Used by |
|---|---|---|
| Cancel undo window | 30 s | `CancelRideCommand` |
| Driver no-show auto-cancel | 5 min | lifecycle → `Cancelled`, then re-search |
| Ride offer countdown | 15 s | driver-mode offer sheet |
| Location ping, en route | 3 s | `LocationPublisher` |
| Location ping, idle | 5 s | `LocationPublisher` |

## Tests

- Location: `domain/src/test/kotlin/...`, mirroring the main package.
- Required coverage: every use case, every `FareStrategy`, `FareStrategyFactory`, every
  validator in the chain, and the `RideState` transitions as a full event × state matrix
  (every invalid pair asserts the state is unchanged).