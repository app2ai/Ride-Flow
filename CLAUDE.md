# RideFlow — Claude Code Project Rules

RideFlow is a real-time ride-hailing Android app (Rider + Driver) built with
Clean Architecture + MVI, multi-module, Jetpack Compose, and a Spring Boot backend.
Read this file fully before every task. When a rule here conflicts with a general
habit, this file wins.

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
| Backend | Spring Boot (separate repo: `rideflow-backend`) |
| Real-time | WebSocket |
| Local storage | Room + DataStore |
| Network | Retrofit + OkHttp |
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

### Convention plugins

| Plugin ID | Apply to |
|---|---|
| `rideflow.kotlin.library` | `:domain` |
| `rideflow.android.library` | `:data`, `:core:*` |
| `rideflow.android.feature` | every `:feature:*` |

A new module must apply exactly one of these. Do not duplicate config they already provide.

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

| Concern | Pattern |
|---|---|
| Fare calculation | Strategy + `FareStrategyFactory` |
| Ride lifecycle | State (sealed `RideState`) |
| Location / status streams | Observer (Flow) |
| Cancel with undo, payment retry | Command |
| Ride request creation | Builder |
| Request validation | Chain of Responsibility |
| GPS smoothing, caching | Decorator |
| External ↔ domain models | Adapter (mappers) |

---

## 7. Kotlin & Code Style

- Use `sealed interface` for closed hierarchies, with exhaustive `when` and no `else` branch.
- No `!!`. Handle nullability explicitly.
- Prefer `val` and immutable collections.
- Never use `GlobalScope`. Use `viewModelScope` or injected scopes.
- Inject `CoroutineDispatcher`s; never hardcode `Dispatchers.IO` inside classes.
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
- Never use LiveData, RxJava, or `GlobalScope`.
- Never let one feature module import another.
- Never put business logic in a Composable or a Reducer side effect.
- Never hardcode versions, keys, or user-facing strings.
- Never skip a layer "because it's simple".
