---
name: add-fare-strategy
description: Adds a new fare calculation strategy to RideFlow's Strategy-pattern fare engine — a new FareStrategy implementation, its registration in FareStrategyFactory, a VehicleType entry if needed, and full unit tests — without modifying existing strategy classes (Open/Closed). Use this whenever the user wants a new vehicle type, ride category, pricing rule, surge rule, discount model or fare formula (e.g. "add electric car pricing", "add a night fare", "support bike taxis"), even if they don't mention strategies or the factory.
---

# Add Fare Strategy

Extends the fare engine in `:domain/fare/` following Open/Closed: new behaviour is a
new class. Existing strategy classes are never edited.

## Step 1 — Gather inputs

| Input | Example |
|---|---|
| Strategy name | `ElectricCarFareStrategy` |
| New `VehicleType`? | yes → `ELECTRIC_CAR` |
| Base fare | ₹40 |
| Per-km rate | ₹9 |
| Per-minute rate | ₹1.5 |
| Minimum fare | ₹60 |
| Surge applies? | yes / no / capped at X |
| Selection rule in factory | `vehicleType == ELECTRIC_CAR` |

Ask for any rate that isn't given. Never invent pricing numbers silently; if the user
says "pick sensible defaults", choose them and list them clearly in the report.

## Step 2 — Implement

Location: `domain/src/main/kotlin/com/rtech/rideflow/domain/fare/`

1. Create `<Name>FareStrategy.kt` implementing `FareStrategy`.
   - Pure Kotlin. No Android imports, no I/O, deterministic for the same inputs.
   - Rates are named constants in a `private companion object`, not magic numbers.
   - Money in `Long` paise (minor units). Round once, at the end.
   - Enforce the minimum fare.
   - Return a full `Fare` breakdown (base, distance, time, surge multiplier, tax, total).
   - KDoc explaining the formula.
2. If a new vehicle type is needed, add it to `VehicleType`. This is the one existing file
   that may change, because adding an enum/sealed entry is extension, not modification of logic.
3. Register the strategy in `FareStrategyFactory`.
   - If the factory uses a registry map, add one entry.
   - If it still uses a `when`, add one branch and flag in the report that converting the
     factory to a registry map would remove even this edit. Don't refactor unless asked.
4. If surge applies, compose with the existing surge logic (wrap or reuse `SurgeFareStrategy`);
   don't copy its math.

Do not modify `StandardFareStrategy`, `SurgeFareStrategy`, `SharedFareStrategy`,
`AutoFareStrategy`, or any other existing strategy.

## Step 3 — Tests

Location: `domain/src/test/kotlin/.../fare/<Name>FareStrategyTest.kt` — JUnit4, no mocks needed.

Cover at least:
- normal trip → exact expected total
- zero-distance trip → minimum fare applies
- long trip → correct distance component
- surge on → multiplier applied (or cap respected); surge off → ignored
- breakdown parts add up to total

Add to `FareStrategyFactoryTest`:
- the new selection rule returns `<Name>FareStrategy`
- existing selection rules still return their original strategies

## Step 4 — UI (only if a new VehicleType was added)

- Add the label string resource and icon in `:feature:ride-booking`.
- Ensure the vehicle selector maps the new type. Exhaustive `when` will fail compilation
  until it's handled — that's intended.

## Step 5 — Verify and report

1. Run `./gradlew :domain:test ktlintCheck detekt` (plus `:feature:ride-booking:testDebugUnitTest` if UI changed).
2. Confirm no existing strategy file appears in the diff.
3. Report: the formula used, files created, files modified (should be only `VehicleType`,
   `FareStrategyFactory`, UI mapping), TODOs flagged as warnings, and test results.
