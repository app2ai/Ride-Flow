# RideFlow — Claude Code Project Rules

RideFlow is a real-time ride-hailing Android app (Rider + Driver) built with
Clean Architecture + MVI, multi-module, Jetpack Compose, and a Spring Boot backend.
Read this file fully before every task. When a rule here conflicts with a general
habit, this file wins.

The reference design is `docs/tech-docs/RideFlow — Android App HLD & LLD.pdf`. Use it for
scope, entities, flows and phase plans. Its code samples are illustrative only: where they
conflict with this file, **this file wins** (known conflicts are listed in §14).

---

## 1. Project Facts

| Item | Value |
|---|---|
| Package name | `com.rtech.rideflow` |
| Min SDK | 29 |
| Kotlin | 2.2.10 |
| Compose | BOM `2026.02.01` + `org.jetbrains.kotlin.plugin.compose` plugin |
| DI | Koin |
| Async | Coroutines + Flow (no LiveData, no RxJava) |
| Maps | Google Maps Compose + Places API + Directions API |
| Payment | Razorpay (test mode during development) |
| Backend | Spring Boot (separate repo: `rideflow-backend`) + PostgreSQL + Redis geo-index |
| Real-time | WebSocket via **OkHttp's WebSocket client** (no Ktor client; one HTTP stack) |
| Push | Firebase Cloud Messaging (ride-status and receipt pushes) |
| Background work | WorkManager (retries mutations queued in Room when back online) |
| Local storage | Room + DataStore |
| Network | Retrofit + OkHttp |
| Date/time | `kotlin.time.Instant` (opt-in to `ExperimentalTime` is set by the convention plugins) and `kotlin.time.Duration`; `kotlinx-datetime` for calendar/time-zone work |
| Static analysis | ktlint (`org.jlleitschuh.gradle.ktlint`, `.editorconfig`) + detekt 2.0 (`dev.detekt`, `config/detekt/detekt.yml`), applied to every module from the root `build.gradle.kts` |
| Build | Gradle Version Catalog (`gradle/libs.versions.toml`) + convention plugins in `build-logic/` |

Never hardcode dependency versions inside a module's `build.gradle.kts`. Always add
them to `libs.versions.toml` and reference via `libs.*`.

---

## 2. Architecture Invariants

These hold for every change, regardless of how small. If a changes violated one,
stop and flag it instead of proceeding.

1. **Dependency direction is one-way and acyclic:** `:domain` ← `:core:*` / `:data` ← `:feature:*` ← `:app`. Nothing ever depends back toward `:app`.
2. **`:domain` is pure Kotlin.** Zero Android/AndroidX imports (no `Context`, no `android.*`, no `androidx.*`), zero third-party framework imports.
3. **`:feature:*` modules never depend on `:data` or on another `:feature:*` module.** They only call `:domain` use cases; Koin (wired in `:app`) supplies the repository implementations at runtime.
4. **Every operation crosses the full layer chain:** Screen → ViewModel → UseCase → Repository interface → Repository impl → data source. No skipping a layer because it's "just a read" or "trivial."
5. **Repository interfaces live only in `:domain`; implementations live only in `:data`.** A ViewModel or feature module never references a `:data` class directly.
6. **DTOs and Room entities never leave `:data`.** They are mapped to domain entities (Adapter pattern) before crossing into `:domain`/`:feature:*`.
7. **Reducers are pure functions.** `(State, Intent) -> State` only — no `suspend`, no coroutines, no I/O, no logging, no exceptions thrown. Invalid transitions return the current state unchanged.
8. **Cross-feature navigation goes only through `:core:navigation` contracts.** No feature ever imports another feature's package to navigate to it.
9. **Each module applies exactly one convention plugin** (`rideflow.kotlin.library`, `rideflow.android.library`, or `rideflow.android.feature`) and does not duplicate the config it already provides with ad hoc `android {}`/`kotlin {}` blocks.
10. **No hardcoded dependency versions, API keys, or user-facing strings** in any module's `build.gradle.kts` or source — versions go in `libs.versions.toml`, keys go in `local.properties`/`BuildConfig`, strings go in resources.

See §3 (module boundaries), §4 (Clean Architecture detail), and §5 (MVI detail) for the full rationale and enforcement detail behind each invariant.

---

## 3. Module Structure & Boundaries

```
:app                 Entry point, Koin startup, root NavHost
:domain              Pure Kotlin — entities, use cases, repository interfaces
:data                Repository implementations, Retrofit, Room, WebSocket, DTOs
:core:network        OkHttp, Retrofit, WebSocketManager, interceptors
:core:location       FusedLocationProvider wrapped as Flow
:core:designsystem   Theme, tokens, shared Compose components
:core:navigation     Destinations + navigation contracts
:core:common         Result wrappers, UiError, extensions
:feature:*           onboarding, home, ride-booking, live-tracking,
                     driver-mode, payment, ride-history, profile
```

### Dependency rules (hard rules — never break)

| Module | May depend on | Must NOT depend on |
|---|---|---|
| `:domain` | nothing (pure Kotlin) | Android SDK, `:data`, `:core:*`, `:feature:*` |
| `:data` | `:domain`, `:core:network`, `:core:common` | `:feature:*`, `:app` |
| `:core:*` | `:domain` (models only), `:core:common` | `:data`, `:feature:*` |
| `:feature:*` | `:domain`, `:core:*` | `:data`, any other `:feature:*` |
| `:app` | everything | — |

- Cross-feature navigation goes only through `:core:navigation`.
- Features never import `:data`. They talk to use cases only; Koin wires implementations in `:app`.
- `:data` is a **single module**. Where the HLD/LLD says `:data:local` or `:data:remote`, read
  it as the `local/` and `remote/` packages inside `:data`. Do not create submodules.
- Domain packages are singular: `model/`, `usecase/`, `repository/` (not the PDF's
  `entities/`, `usecases/`, `repositories/`). See `domain/CLAUDE.md`.
- Module edges use `implementation(...)`. Use `api(...)` only when the module's public
  signatures expose the other module's types, with a comment saying why (see
  `domain/build.gradle.kts`).
- Rider vs Driver: shared logic goes in `:domain` / `:core:*`; role-specific screens live in
  their own feature modules (e.g. `:feature:driver-mode`). No `if (isDriver)` scattered
  through shared UI.

### Convention plugins

| Plugin ID | Apply to |
|---|---|
| `rideflow.kotlin.library` | `:domain` |
| `rideflow.android.library` | `:data`, `:core:*` |
| `rideflow.android.feature` | every `:feature:*` |

A new module must apply exactly one of these. Do not duplicate config they already provide.

### Adding a new module

1. For a feature, use the `create-mvi-feature` skill (§10) instead of doing this by hand.
2. Apply exactly one convention plugin from the table above.
3. Add only edges allowed by the dependency table. If a new edge is justified, update the
   table in the same change.
4. Give it its own Koin module and register it in `:app`'s `startKoin`.
5. Add it to `settings.gradle.kts`, then run `./gradlew assembleDebug ktlintCheck detekt`.

---

## 4. Clean Architecture — Always Enforced

Full Clean Architecture is mandatory. No shortcuts, even for trivial operations.

- Every operation flows: **Screen → ViewModel → UseCase → Repository interface → Repository impl → data source**.
- A ViewModel never calls a repository directly, even for a one-line read.
- Every use case has a single public `operator fun invoke(...)`.
- Use cases return `Flow<Result<T>>` for streams, `Result<T>` for one-shot calls.
- Repository interfaces live in `:domain`; implementations live in `:data`.
- DTOs and Room entities never leave `:data`. Map them to domain entities in `:data`.
- `:domain` has zero Android imports: no `Context`, no `android.*`, no `androidx.*`.

If a request seems to need a shortcut, stop and explain the proper layered approach instead.

---

## 5. MVI Rules

Every feature has exactly these files:

```
feature/<name>/
  <Name>Contract.kt     State, Intent, Effect
  <Name>Reducer.kt      pure reduce function
  <Name>ViewModel.kt    dispatches intents, runs use cases
  <Name>Screen.kt       Compose UI
  di/<Name>Module.kt    Koin module
```

- **State** is a single immutable `data class` with defaults.
- **Intent** and **Effect** are `sealed interface` hierarchies.
- **Reducer** is a pure function `(State, Intent) -> State`: no `suspend`, no coroutines, no I/O, no logging.
- Invalid state transitions return the current state unchanged. Never throw.
- ViewModel exposes `StateFlow<State>` and `Flow<Effect>`. `MutableStateFlow` and `Channel` stay private.
- Every Intent is handled in `dispatch()` through an exhaustive `when` with no `else` branch.
  Intents that only change state map explicitly to `Unit`. No silent drops.
- Anything time- or randomness-dependent (timestamps, IDs) is produced in the ViewModel or use
  case and carried on the Intent. The reducer never calls `Clock.System.now()`.
- Navigation, toasts, and snackbars happen only through Effects.
- Screens hold no business logic. Only UI-only state (animation, scroll, focus) may use `remember`.
- Screens never call use cases directly. They only call `viewModel.dispatch(intent)`.
- Split each screen into a stateful `<Name>Route` (gets the ViewModel) and a stateless `<Name>Screen(state, onIntent)` so previews and UI tests don't need a ViewModel.

---

## 6. SOLID & Design Patterns

- Prefer adding a new class over modifying a `when` block (Open/Closed). New fare types, validators, and event handlers are new classes.
- Keep interfaces small and role-based, e.g. `LocationPublisher` vs `LocationSubscriber`, not one `LocationRepository` with everything.
- Depend on interfaces, never on concrete implementations, across module boundaries.

Patterns already chosen for this project (reuse them, don't reinvent):

| Concern | Pattern | Concrete home |
|---|---|---|
| Fare calculation | Strategy | `FareStrategy` impls in `:domain` (`StandardFareStrategy`, `SharedFareStrategy`, `AutoFareStrategy`, …) |
| Strategy selection | Factory | `FareStrategyFactory` in `:domain` |
| Surge pricing | Decorator | `SurgeFareStrategy` wraps another `FareStrategy`; it never instantiates one itself |
| Ride lifecycle | State | sealed `RideState` + transitions in one reducer (see `domain/CLAUDE.md`) |
| Location / status streams | Observer | `Flow` / `StateFlow` |
| Cancel with undo, payment retry | Command | `CancelRideCommand`, `RetryPaymentCommand` |
| Ride request creation | Builder | `RideRequest.Builder` in `:domain` |
| Request validation | Chain of Responsibility | `RideRequestValidator` chain in `:domain` |
| GPS smoothing, caching | Decorator | `SmoothedLocationSource`, `CachedFareRepository` in `:data` |
| External ↔ domain models | Adapter | DTO/entity mappers in `:data`; `GoogleLatLngAdapter` |
| Data access | Repository + Facade | repository impls hide Room + REST + WebSocket |
| Shared socket | Singleton (scoped) | `WebSocketManager` as a Koin `single` in `:core:network` |
| Use-case error/loading handling | Template Method | `BaseRideUseCase`: public `operator fun invoke` stays the only entry point, subclasses override a `protected` `execute()` |

- `FareStrategyFactory`: prefer a registry (`Map<VehicleType, FareStrategy>` supplied by Koin) so
  a new strategy is a new class plus a registration. If a `when` is used, it must be exhaustive
  with no `else`.
- `GoogleLatLngAdapter` touches Google Maps types, so it lives in `:core:location` (or the
  feature that renders the map), never in `:domain`.

---

## 7. Kotlin & Code Style

- Use `sealed interface` for closed hierarchies, with exhaustive `when` and no `else` branch.
- No `!!`. Handle nullability explicitly.
- Prefer `val` and immutable collections.
- Never use `GlobalScope`. Use `viewModelScope` or injected scopes.
- Never use `runBlocking` in production code (tests use `runTest`).
- Inject `CoroutineDispatcher`s; never hardcode `Dispatchers.IO` inside classes.
- Wrap callback APIs (Maps, FCM, FusedLocationProvider) with `callbackFlow` in the module that
  owns the source (`:core:location`, `:data`), never in UI code.
- Constructor injection only. `KoinComponent` / `by inject()` is allowed only in Android entry
  points (Activity, Application, Service, Worker, FCM service).
- Format with ktlint; static analysis with detekt. Code must pass both.
- No magic numbers. Use named constants.
- Strings go in resources; never hardcode user-facing text in Compose.

### KDoc

Write KDoc for every `public` class, function, and property. Include:
- one-line summary
- `@param` for each parameter
- `@return` when non-Unit
- `@throws` only if it actually throws

### TODOs

When you see or write a `TODO` / `FIXME`, flag it as a warning in your response with its file and line. Never leave a TODO without a short reason, e.g. `// TODO(phase-7): replace mock with WebSocket source`.

---

## 8. Testing

### Unit tests (JUnit4)

- **MockK** is the default mocking library for all Kotlin code.
- Use **Mockito** only when mocking Java-based third-party classes where MockK has problems. Add a one-line comment explaining why.
- Use **Turbine** to test every `Flow`.
- Use `kotlinx-coroutines-test` (`runTest`, `StandardTestDispatcher`).
- Every use case: happy path plus at least 2 error paths.
- Every reducer: a test for every Intent, including invalid transitions. Test the reducer directly; never mock it.
- Test name format: `` `given <state>, when <action>, then <result>`() ``.
- State-machine reducers (anything driving `RideState`, `PaymentState`) get a full
  **Intent × State** matrix: every intent tested from every state, with invalid pairs
  asserting the state is unchanged.

### Integration tests

- WebSocket events: integration tests against a local mock server (`libs.okhttp.mockwebserver`,
  `mockwebserver3`) that cover connect, every event type parsed and mapped to domain, and
  reconnect after a drop.

### UI tests

- **Compose screens:** Compose UI Test APIs (`createAndroidComposeRule`, `onNodeWithTag`) on the AndroidJUnit4 runner. Espresso cannot interact with Compose nodes.
- **Espresso:** Activity launch, intents, system and permission dialogs, and any View-based interop.
- Every interactive composable gets a `Modifier.testTag(...)`; tag constants live in the feature's `TestTags.kt`.

Do not mark a task complete until its tests are written and passing.

---

## 9. Git Conventions

| Type | Branch format | Example |
|---|---|---|
| Feature | `feature/<short-name>` | `feature/ride-booking` |
| Bug / fix | `bug/<short-name>` | `bug/ride-issue-fix` |

- Branch names: lowercase, hyphen-separated.
- Never commit directly to `main`.
- Never commit API keys. Google Maps and Razorpay keys go in `local.properties` and are read via `BuildConfig`/secrets plugin.

---

## 10. Skills

Use these project skills (in `.claude/skills/`) instead of writing boilerplate by hand:

| Skill | Use when |
|---|---|
| `create-mvi-feature` | Creating any new feature module or screen |
| `add-fare-strategy` | Adding a new vehicle type or fare rule |

If a task matches a skill, use the skill. Don't hand-write the same scaffold.

More skills, subagents, MCP servers and hooks are **planned but not built yet**. They're tracked in
`.claude/tech-plan/roadmap.md`. Don't try to invoke one until it exists under `.claude/`.

---

## 11. Before Finishing Any Task

1. Architecture invariants respected (section 2).
2. Module boundaries respected (section 3).
3. Full layer chain used (section 4).
4. Reducer is pure (section 5).
5. KDoc on all new public APIs.
6. TODOs flagged in your response.
7. Unit tests written and passing.
8. `./gradlew ktlintCheck detekt` passes.
9. Summarize which files changed and why.

---

## 12. Never Do

- Never add Android imports to `:domain`.
- Never call a use case or repository from a Composable.
- Never use LiveData, RxJava, `GlobalScope`, or `runBlocking` in production code.
- Never let one feature module import another.
- Never put business logic in a Composable or a Reducer side effect.
- Never hardcode versions, keys, or user-facing strings.
- Never skip a layer "because it's simple".
- Never copy a code sample from the HLD/LLD PDF verbatim. Check it against §14 first.

---

## 13. Build Phases

Each phase ends with a working build. Don't start the next phase without one.
Full feature tables are in the HLD/LLD PDF; tooling status is in `.claude/tech-plan/roadmap.md`.

| # | Phase | Main modules |
|---|---|---|
| 1 | Foundation: module skeleton, Gradle, `:domain` entities and repository interfaces, Koin base | all |
| 2 | Auth: phone + OTP, JWT storage, profile setup, auth interceptor | `:feature:onboarding`, `:data`, `:core:network` |
| 3 | Home map: Compose Maps, location Flow, Places autocomplete, recent places | `:feature:home`, `:core:location` |
| 4 | Ride booking: vehicle selection, mocked fare estimate, Builder, validation chain | `:feature:ride-booking`, `:domain` |
| 5 | Fare engine: real strategies, surge, factory | `:domain` |
| 6 | WebSocket Simulator MCP (built before live tracking) | `.claude/` + external server |
| 7 | Live tracking: driver pin, ETA, polyline, status banner | `:feature:live-tracking`, `:core:network`, `:data` |
| 8 | Ride lifecycle: full state machine, cancel with undo, no-show timeout | `:domain`, `:feature:live-tracking` |
| 9 | Driver mode: online toggle, location publishing, ride offers, navigation | `:feature:driver-mode` |
| 10 | Payment: Razorpay, receipt, retry | `:feature:payment` |
| 11 | Architecture review and polish | all |
| 12 | Real backend: replace mocks | `:data` |

**Current phase: 1 (Foundation).** Done: version catalog, convention plugins, module graph,
root/`:domain`/`:data` CLAUDE.md, `create-mvi-feature` skill, ktlint/detekt.
Remaining: `:domain` entities, repository interfaces, Koin base wiring, a CI check that
`:domain` has no Android dependencies. Update this line when a phase completes.

---

## 14. Known HLD/LLD Sample Conflicts (this file wins)

| PDF sample | Do this instead |
|---|---|
| `sealed class` for State/Intent/Effect/RideState | `sealed interface` (§7) |
| Fare amounts as `Double` (`30.0`, `12.0`) | `Long` paise (`domain/CLAUDE.md`) |
| `FareStrategyFactory` `when` with `else` | Registry map or exhaustive `when` (§6) |
| `SurgeFareStrategy` calls `StandardFareStrategy()` | Decorator wrapping an injected `FareStrategy` (§6) |
| Reducer calls `Clock.System.now()` | Timestamp carried on the Intent (§5) |
| `suspend fun requestRide(): Flow<…>` | Non-suspend `fun …(): Flow<Result<…>>` (§4) |
| ViewModel injects `FareStrategy` directly (LSP example) | ViewModel → `CalculateFareUseCase` (§4) |
| Screen takes the ViewModel and navigates with `"tracking/$id"` | `Route`/`Screen` split; navigate through `:core:navigation` contracts (§5) |
| `Effect.ShowError(message: String)` | Carry `UiError` / a string-resource id (§7) |
| `dispatch()` with `else -> Unit` | Exhaustive `when` (§5) |
| One `LocationRepository` (p.5) | `LocationSubscriber` + `LocationPublisher` (§6) |
| Validators return `Result.failure(SomeException())` | Sealed domain errors (`domain/CLAUDE.md`) |
| Ktor backend / Ktor WebSocket client | Spring Boot backend; OkHttp WebSocket (§1) |
| `:data:local` / `:data:remote` modules | Packages inside `:data` (§3) |
