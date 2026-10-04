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
  remote/
    api/          Retrofit service interfaces
    websocket/    WebSocket event DTOs and data sources built on :core:network's WebSocketManager
    dto/          Response/request DTOs
  local/
    room/         Room database, DAOs, entities (incl. the offline mutation queue)
    preferences/  DataStore sources (incl. the encrypted token store)
  mapper/         DTO/entity <-> domain mappers (Adapter pattern)
  repository/     Implementations of :domain repository interfaces (+ Decorators below)
```

`local/` and `remote/` are packages, not modules. The HLD/LLD's `:data:local` and
`:data:remote` refer to these packages.

## Rules

- Each repository implementation implements one `:domain` interface and is `internal`
  where Koin wiring in `:app` allows it.
- Map at the boundary: data sources speak DTOs/entities, repositories return domain types.
- Convert exceptions to sealed domain errors inside the repository; never rethrow raw
  `IOException`/`HttpException` to callers.
- Inject `CoroutineDispatcher`s; never reference `Dispatchers.*` directly.
- Repository impls are Facades: callers never learn whether data came from Room, REST or the
  WebSocket.
- The WebSocket connection is the single `WebSocketManager` owned by `:core:network` (a Koin
  `single`, OkHttp client, auto-reconnect). Data sources here subscribe to its events and map
  them; they never open their own socket.
- Decorators wrap the interface, never subclass: `SmoothedLocationSource` wraps the raw
  location source; `CachedFareRepository` wraps the remote `FareRepository`.
- JWT/refresh tokens live only in an encrypted (Tink-backed) DataStore. Never in plain
  DataStore, SharedPreferences, Room or logs.
- Failed mutations are queued in Room and replayed by WorkManager when connectivity returns.

## Tests

- Location: `data/src/test/kotlin/...`, mirroring the main package.
- Required coverage: every mapper, and every repository implementation (happy path plus
  at least 2 error paths) with data sources mocked via MockK and Flows tested with Turbine.
- WebSocket integration tests run against `mockwebserver3` (`libs.okhttp.mockwebserver`):
  connect, every event type mapped to its domain type, malformed payload → sealed error, and
  reconnect after the server drops.
