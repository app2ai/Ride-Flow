---
name: create-mvi-feature
description: Scaffolds a complete Clean MVI feature module or screen for RideFlow — Contract (State/Intent/Effect), pure Reducer, ViewModel, stateless Screen + Route, Koin module, test tags, and unit/UI test files, then wires it into settings.gradle.kts, :app Koin startup and :core:navigation. Use this whenever the user asks to create, add, start, or scaffold a feature, module, screen, or flow (e.g. "add the payment feature", "start Phase 2 onboarding", "create a ride-history screen"), even if they don't say "MVI" or "scaffold".
---

# Create MVI Feature

Generates a new feature that follows the root `CLAUDE.md` exactly. Never hand-write
these files when this skill applies.

## Step 1 — Gather inputs

Confirm these before writing files. Infer from the request where possible; ask only for what's missing.

| Input | Example |
|---|---|
| Feature name (kebab-case module) | `ride-booking` |
| PascalCase name | `RideBooking` |
| New module or new screen in existing module | new module |
| State fields | `pickup: Address?`, `isLoading: Boolean` |
| Intents | `SetPickup`, `RequestRide` |
| Effects | `NavigateToTracking(rideId)`, `ShowError` |
| Use cases needed | `RequestRideUseCase` (existing or new) |

## Step 2 — Create files

Module path: `feature/<feature-name>/`
Package: `com.rtech.rideflow.feature.<featurename>` (no hyphens in package)

```
feature/<feature-name>/
  build.gradle.kts                 plugins { id("rideflow.android.feature") }
  src/main/kotlin/com/rtech/rideflow/feature/<featurename>/
    <Name>Contract.kt
    <Name>Reducer.kt
    <Name>ViewModel.kt
    <Name>Screen.kt                <Name>Route (stateful) + <Name>Screen (stateless)
    <Name>TestTags.kt
    di/<Name>Module.kt
  src/test/kotlin/.../
    <Name>ReducerTest.kt
    <Name>ViewModelTest.kt
  src/androidTest/kotlin/.../
    <Name>ScreenTest.kt
```

### File requirements

**Contract**
- `data class State` with a default for every field, including `isLoading = false` and `error: UiError? = null`.
- `sealed interface Intent` and `sealed interface Effect`.

**Reducer**
- `fun reduce(state: State, intent: Intent): State` — pure.
- Exhaustive `when` over Intent with no `else`.
- Async-trigger intents only set loading flags; the ViewModel does the work.

**ViewModel**
- Constructor-injects use cases and a `CoroutineDispatcher`.
- Private `MutableStateFlow<State>` exposed as `StateFlow`; private `Channel<Effect>(BUFFERED)` exposed via `receiveAsFlow()`.
- `fun dispatch(intent: Intent)`: run reducer first, then launch side effects for async intents.
- Map failures to `UiError` from `:core:common`.

**Screen**
- `<Name>Route`: gets the ViewModel with `koinViewModel()`, collects state with `collectAsStateWithLifecycle()`, collects effects in `LaunchedEffect`, and calls the stateless screen.
- `<Name>Screen(state: State, onIntent: (Intent) -> Unit, modifier: Modifier = Modifier)`: no ViewModel, no business logic.
- `Modifier.testTag(...)` on every interactive element, using constants from `<Name>TestTags`.
- At least one `@Preview` of the stateless screen.
- All text from `stringResource`.

**Koin module**
- `val <name>Module = module { viewModelOf(::<Name>ViewModel) }` plus any new use case `factoryOf(...)`.

**KDoc** on every public class and function.

## Step 3 — Create or reuse use cases

- Reuse existing use cases in `:domain` when they fit.
- If new ones are needed, create them in `domain/.../usecase/<area>/` following `domain/CLAUDE.md`, plus their repository interface methods. Never skip the use case layer.

## Step 4 — Wire it up

1. Add `include(":feature:<feature-name>")` to `settings.gradle.kts`.
2. Add `implementation(projects.feature.<featureName>)` to `:app`.
3. Register `<name>Module` in the `:app` Koin startup list.
4. Add the destination to `:core:navigation` and the `NavHost` in `:app`.

## Step 5 — Tests

- **ReducerTest**: one test per Intent, plus invalid-transition tests. Call `reduce` directly. JUnit4.
- **ViewModelTest**: MockK use cases, `runTest` + `StandardTestDispatcher`, Turbine for both `state` and `effects`. Happy path plus at least 2 error paths.
- **ScreenTest**: Compose UI Test (`createComposeRule`) against the stateless `<Name>Screen`, using test tags. Use Espresso only for Activity-level or system interactions.
- Naming: `` `given <state>, when <action>, then <result>`() ``.

## Step 6 — Verify and report

1. Run `./gradlew :feature:<feature-name>:testDebugUnitTest ktlintCheck detekt`.
2. Fix failures before reporting done.
3. Report: files created, files modified, any TODOs (flagged as warnings with file + line), and next steps.
