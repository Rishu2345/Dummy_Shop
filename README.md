# DummyShop

An Android e-commerce browsing app built as the final selection task for the **IonXtech Android Developer Internship**. DummyShop fetches product data from the [DummyJSON](https://dummyjson.com/) API and presents it as a scrollable, searchable, paginated product catalog with offline caching, built entirely in Jetpack Compose.

---

## Table of Contents

- [Project Description](#project-description)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [How It Maps to the Assessment Brief](#how-it-maps-to-the-assessment-brief)
- [Screenshots](#screenshots)
- [Build Instructions](#build-instructions)
- [Known Limitations](#known-limitations)

---

## Project Description

DummyShop is a single-activity Compose app with four main destinations reachable via a bottom navigation bar: **Home**, **Search**, **Cart**, and **Profile**. Home and Search are the fully-built experiences — they load products from the DummyJSON REST API, cache them locally in a Room database, and support infinite scroll pagination via Paging 3's `RemoteMediator`. Cart and Profile are intentionally kept as simple placeholder screens, since the assessment brief calls for one solid vertical slice rather than a fully-featured shopping app.

## Features

**Core requirements (from the brief):**
- Scrollable list of products fetched from a public REST API (DummyJSON)
- Clean Material 3 UI with proper spacing, alignment, and adaptive layout
- Graceful loading and error states, with retry
- MVVM-structured, readable, single-responsibility code

**Bonus features implemented:**
- **Search functionality** — debounced (300ms) live search against `/products/search`, with a "Recent searches" list and "Trending" suggestion chips
- **Pagination** — Paging 3 + `RemoteMediator`, loads more products as the user scrolls, with a footer loader/retry for failed page loads
- **Local data caching / Offline support** — Room database mirrors network results; previously loaded products remain viewable without a network connection
- **Clean Architecture / MVVM** — clear separation between `network` (API + RemoteMediator), `db` (Room), `repository` (single source of truth), `viewModel` (UI state), and `view` (Compose UI)
- **Dark Mode support** — Material 3 dynamic color theming, respects system theme
- **Animations** — `AnimatedContent`/`AnimatedVisibility` for loading ↔ content ↔ error transitions
- **Pull-to-refresh** — manual refresh gesture on both Home and Search grids
- **Unit tests** — coverage for entity mapping and shared UI components

**Not implemented (by design, given scope/time):** Cart and Profile are stub "Under construction" screens — no cart logic, checkout, or user accounts, since these are out of scope for the brief's core requirement of "display a list of items via API integration."

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| DI | Koin |
| Networking | Retrofit + OkHttp + kotlinx.serialization |
| Local storage | Room (with Paging 3 integration) |
| Pagination | Paging 3 (`Pager`, `RemoteMediator`) |
| Async | Kotlin Coroutines + Flow |
| Image loading | Coil |
| Navigation | Jetpack Navigation Compose |
| Testing | JUnit, Compose UI Test |

**SDK levels:** `minSdk 27`, `targetSdk 36`, `compileSdk 37`.

## Architecture

The app follows a layered MVVM structure:

```
UI (Compose screens)
   ↕ collects StateFlow / LazyPagingItems
ViewModel (per-feature: Home, Search, Product detail)
   ↕ calls suspend/Flow functions
Repository (ProductRepository — single source of truth)
   ↕
   ├── Network (Retrofit ApiService) ──┐
   │                                    ├── RemoteMediator merges network → DB
   └── Local DB (Room: ProductDao,      │    on each page load
       RemoteKeysDao)  ←────────────────┘
```

- **`ProductRepository`** builds a `Pager` for each query type (all products, by category, search, sorted) backed by a Room `PagingSource` and a `ProductRemoteMediator`.
- **`ProductRemoteMediator`** fetches the next page from DummyJSON, stores results in Room, and tracks per-query pagination cursors in a `remote_keys` table (tagged by query type, so "all products," a category filter, and a search query each track their own independent pagination state).
- **ViewModels** expose a `StateFlow<UiState>` for screen-level state (selected category, search query, recent searches) and a separate `Flow<PagingData<T>>` for the actual list content, consumed via `collectAsLazyPagingItems()`.
- **UI** is fully declarative Compose, reacting to `LoadState` (`Loading` / `NotLoading` / `Error`) for both initial load (`refresh`) and infinite-scroll (`append`).

## Project Structure

```
app/src/main/java/com/assasement/dummyShop/
├── MainActivity.kt              # Single activity, hosts NavHost + bottom nav
├── di/                          # Koin modules, Application class
├── model/                       # API response DTOs (Product, Category, Review, etc.)
├── network/                     # ApiService (Retrofit), ProductQuery, ProductRemoteMediator
├── db/                          # Room: AppDatabase, ProductDao, RemoteKeysDao, entities
├── repository/                  # ProductRepository — builds Pagers, single source of truth
├── utils/                       # Entity ↔ model mappers
├── viewModel/
│   ├── homeScreenViewModel/      # HomeViewModel
│   ├── searchViewModels/         # SearchViewModel
│   └── productViewModel/         # ProductDetailViewModel
├── view/
│   ├── home/                     # HomeScreen
│   ├── search/                   # SearchScreen
│   ├── product/                  # Product detail screen
│   └── components/               # Shared composables (ProductCard, loaders, error/empty states)
├── navigation/                   # AppNavGraph — sealed route definitions, bottom nav
└── ui/theme/                     # Material 3 theme, color scheme, typography
```

## How It Maps to the Assessment Brief

| Brief requirement | Where it lives |
|---|---|
| Display a list of items | `HomeScreen.kt` — `LazyVerticalGrid` of products |
| API integration | `ApiService.kt` (Retrofit → DummyJSON) |
| Material Design UI | Material 3 components throughout, `ui/theme/` |
| Loading/error states | `LoadState` handling in `HomeScreen.kt` / `SearchScreen.kt` |
| Clean code structure | Layered packages (`network`/`db`/`repository`/`viewModel`/`view`) |
| GitHub repo + README + screenshots + build steps | This repository |
| Bonus: search, pagination, caching, MVVM, dark mode, animations | See [Features](#features) above |

## Screenshots

<table>
  <tr>
    <th>Home</th>
    <th>Product Detail</th>
    <th>Search (suggestions)</th>
    <th>Search (results)</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/6aa4f1f8-8650-4f25-82cc-efdb972ba1f3" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/7930d1ca-8ade-4e7a-ae06-1defe22e563e" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/fb8aa423-2585-4688-91fe-7912c9d5eefb" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/eb5e3c94-56ef-4a21-ad8d-aebd83488d71" width="200"/></td>
  </tr>
</table>

## Build Instructions

**Prerequisites:**
- Android Studio (recent stable version, e.g. Ladybug/Koala or newer)
- JDK 11+
- Android SDK with API 36/37 installed

**Steps:**

1. Clone the repository:
   ```bash
   git clone https://github.com/Rishu2345/Dummy_Shop.git
   cd Dummy_Shop
   ```
2. Open the project in Android Studio (**File → Open** → select the project root).
3. Let Gradle sync finish (downloads dependencies automatically — no API keys or `local.properties` setup needed, since DummyJSON requires no auth).
4. Run on an emulator or physical device (API 27+):
   - Via Android Studio: click **Run ▶**
   - Via command line:
     ```bash
     ./gradlew installDebug
     ```
5. To build an APK directly:
   ```bash
   ./gradlew assembleDebug
   ```
   The output APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

**Running tests:**
```bash
./gradlew test              # unit tests
./gradlew connectedAndroidTest  # instrumented UI tests (requires a connected device/emulator)
```

## Known Limitations

- Cart and Profile are placeholder screens only — no persistence or backend logic behind them.
- No authentication/user accounts (out of scope for the brief).
- Sorting UI (price asc/desc) is supported at the repository/query level but not yet exposed as a control in the Home UI.
