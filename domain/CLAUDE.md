# :domain — Module Rules

This module is **pure Kotlin**. It is the core of RideFlow and must stay independent
of Android, frameworks, and every other module. These rules add to the root `CLAUDE.md`.

## Allowed

- Kotlin standard library
- `kotlinx-coroutines-core` (Flow, suspend)
- `kotlinx-datetime` (Instant, Duration)
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
                  Value types: VehicleType, RideState, CancelReason
  repository/     Interfaces only: RideRepository, LocationSubscriber, LocationPublisher,
                  FareRepository, AuthRepository, PaymentRepository
  usecase/        One class per operation, grouped by area (ride/, fare/, location/, auth/, payment/)
  fare/           FareStrategy interface, strategy implementations, FareStrategyFactory
  validation/     RideRequestValidator chain
  error/          Domain error types (sealed)
```

## Rules

- Entities are immutable `data class`es. No mutable `var` properties.
- `RideState` is a `sealed interface`. Every transition is defined in one place.
- Use cases expose a single `operator fun invoke(...)` and depend only on repository interfaces.
- Return `Result<T>` or `Flow<Result<T>>`. Represent failures with sealed domain errors, not raw exceptions.
- Inject a `CoroutineDispatcher` when a use case needs one; never reference `Dispatchers.*` directly.
- Money is never `Double` in new code paths: use `Long` paise (minor units) for fare totals.
- Every class here must be testable with plain JUnit4 + MockK, with no Robolectric and no emulator.

## Tests

- Location: `domain/src/test/kotlin/...`, mirroring the main package.
- Required coverage: every use case, every `FareStrategy`, `FareStrategyFactory`, every `RideState` transition.