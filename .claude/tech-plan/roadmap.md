# RideFlow — Claude Code Tooling Roadmap

Source: `docs/tech-docs/RideFlow — Android App HLD & LLD.pdf` ("Claude Code Setup" and "Build Order").
Only items marked **Built** exist. Don't invoke anything else until it's built and this file is updated.

## Skills (`.claude/skills/`)

| Skill | Trigger | What it does | Status | Target phase |
|---|---|---|---|---|
| `create-mvi-feature` | "add feature X" | Contract, Reducer, ViewModel, Route/Screen, Koin module, tests | **Built** | 1 |
| `add-fare-strategy` | "add vehicle type X" | Strategy class, factory registration, unit tests | **Built** | 1 |
| `generate-usecase` | "add use case X" | UseCase, repository method, tests | Planned | 2 |
| `write-reducer-tests` | "test reducer X" | Exhaustive Intent × State matrix tests | Planned | 4 |
| `add-websocket-event` | "add event X" | Event DTO, mapper, MVI Intent binding, mock-server test | Planned | 7 |

## Subagents (`.claude/agents/`)

| Agent | Role | Status | Target phase |
|---|---|---|---|
| `architecture-guardian` | Module-boundary and SOLID checks (no Android in `:domain`, no feature→feature imports, pure reducers, repository interface ↔ impl parity). Output: PASS or violations with file + line. | Planned | 2 (full run in 11) |
| `test-writer` | Writes unit tests for Reducers and UseCases; never mocks the reducer | Planned | 5 |
| `lld-doc-agent` | Keeps Mermaid (`.mmd`) state/class diagrams in `docs/` in sync after changes to `RideState`, entities or repository interfaces | Planned | 8 (full sync in 11) |

## MCP servers (`.mcp.json` at the repo root)

| Server | Purpose | Status | Target phase |
|---|---|---|---|
| WebSocket Simulator (custom) | Simulate driver location pings and ride events without a backend (`simulateDriverLocation`, `triggerRideEvent`) | Planned, **build first** | 6 |
| Google Maps | Geocoding, directions, snap-to-road | Planned | 3 |
| ADB | Deploy to emulator, instrumented tests, logcat | Planned | 3 |
| GitHub | Issues, PRs, links to architecture decisions | Planned | 12 |
| Firebase | Send FCM pushes from Claude Code tasks | Planned | 10 |

Note: the PDF says `.claude/mcp.json`. Claude Code reads project MCP servers from `.mcp.json` at the repo root.

## Hooks (`.claude/settings.json`)

| Hook | Event | Command | Status |
|---|---|---|---|
| Lint edited Kotlin file | `PostToolUse` on `Edit\|Write` | `./gradlew ktlintCheck` scoped to the edited module | Planned |
| Domain tests after domain edits | `PostToolUse` on `Edit\|Write` under `domain/` | `./gradlew :domain:test --quiet` | Planned |

The PDF's `pre-edit.sh` / `post-edit.sh` sketch won't work as written: `$CLAUDE_FILE_PATH` isn't
provided (hooks receive JSON on stdin, so the path is read from `tool_input.file_path`), and
linting *before* an edit checks the old content. Use `PostToolUse` instead.

## Build gaps to close

| Gap | Status | Phase |
|---|---|---|
| ktlint + detekt applied to every module | **Done** (root `build.gradle.kts`, `config/detekt/detekt.yml`, `.editorconfig`) | 1 |
| `kotlinx-datetime` in catalog | **Done** (`api` in `:domain`) | 1 |
| `okhttp-mockwebserver` in catalog | **Done** (test dep in `:data`) | 1 |
| CI check that `:domain` has no Android dependencies | Planned | 1 |
| Gradle module-boundary check (fails the build on a forbidden project dependency) | Planned | 2 |
| detekt on `2.0.0-alpha.6` (1.23.8 can't run on the JDK 25 Gradle daemon): move to 2.0.0 stable when released | Planned | 11 |
