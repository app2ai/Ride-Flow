# :data — Module Rules

This module implements the repository interfaces declared in `:domain`. It owns every
external data source: REST (Retrofit), WebSocket, Room and DataStore. These rules add to
the root `CLAUDE.md`.

## Allowed Dependencies

- `:domain`, `:core:network`, `:core:common`
- Retrofit, OkHttp, Room, DataStore, kotlinx-coroutines

## Forbidden

- Dependencies on `:feature:*` or `:app`
- Compose or any UI code
- Leaking DTOs or Room entities out of this module: public APIs expose only domain types

## What Lives Here

```
data/src/main/kotlin/com/rtech/rideflow/data/
  remote/         Retrofit services, WebSocket sources, DTOs
  local/          Room database, DAOs, entities; DataStore sources
  mapper/         DTO/entity <-> domain mappers (Adapter pattern)
  repository/     Implementations of :domain repository interfaces
```

## Rules

- Each repository implementation implements one `:domain` interface and is `internal`
  where Koin wiring in `:app` allows it.
- Map at the boundary: data sources speak DTOs/entities, repositories return domain types.
- Convert exceptions to sealed domain errors inside the repository; never rethrow raw
  `IOException`/`HttpException` to callers.
- Inject `CoroutineDispatcher`s; never reference `Dispatchers.*` directly.

## Tests

- Location: `data/src/test/kotlin/...`, mirroring the main package.
- Required coverage: every mapper, and every repository implementation (happy path plus
  at least 2 error paths) with data sources mocked via MockK and Flows tested with Turbine.
