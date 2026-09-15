# Issue #1 — Juan Pablo Android Foundation

## Purpose

Issue #1 bootstraps the native CampusMeal Android project and the shared architecture that later issues build on. The work is split between two people:

- **Person A, Juan Pablo:** the build foundation (Gradle, AGP, Kotlin plugins, version catalog, dependencies) and the infrastructure boundaries for network, persistence and repositories.
- **Person B, Natalia:** the app shell (activity, root composable, theme, navigation, UI state, initial `AppContainer`, permissions, smoke tests and visual validation).

This document covers Person A's part only. Most of it was delivered in commit `4ac50796dced1f45678c7c243c7c36407effe682`. A follow-up audit on 2026-09-15 closed the remaining gaps, which are listed below.

## Implemented responsibilities

- [x] **Android project in `android/`.** Single `:app` module, Kotlin DSL, namespace and application ID `com.campusmeal.android`. *Already satisfied by `4ac5079`.*
- [x] **Gradle wrapper, AGP and Kotlin plugins.** Gradle 9.5.0 with a pinned distribution checksum, AGP 9.3.2 with built-in Kotlin, and the Compose, Serialization and KSP plugins. *Completed in this run:*
  - The wrapper was regenerated with Gradle 9.5.0. The committed `gradle-wrapper.jar` was the Gradle 9.0.0 jar.
  - `gradlew` is now executable in git.
  - A new `.gitattributes` keeps LF line endings in `gradlew`.
- [x] **Version catalog** (`gradle/libs.versions.toml`). Every pinned version is present and there are no inline versions in build scripts. *Already satisfied.*
- [x] **Compose, Lifecycle, Navigation and Coroutines.** Compose artifacts use the BOM and declare no version. *Already satisfied.*
- [x] **Retrofit, Kotlin Serialization and OkHttp.** *Already satisfied.*
- [x] **Room, DataStore, WorkManager and KSP.** The Room compiler runs through `ksp(...)` and the schema is exported to `app/schemas/`. *Already satisfied.*
- [x] **Initial network, database and repository boundaries.** *Network and database were already satisfied. The repository boundary was completed in this run:* the new `ApiResult`/`apiCall` in the network package keeps Retrofit and OkHttp exceptions out of repositories.
- [x] **Build, test and lint commands documented.** *Already satisfied. This run also fixed two README gaps: the Android SDK location needed for command-line builds, and the instrumented Room test.*
- [x] **Unit tests for the infrastructure.**
  - `ApiClientFactoryTest` already existed.
  - `ApiResultTest` (JVM) and `CampusMealDatabaseTest` (instrumented) were added in this run.

## Main files and components

| File or component | Main class, interface or function | Responsibility | Relation to issue #1 |
| --- | --- | --- | --- |
| [`settings.gradle.kts`](../settings.gradle.kts) | `pluginManagement`, `dependencyResolutionManagement` | Declares the Google, Maven Central and Plugin Portal repositories and includes `:app`. `FAIL_ON_PROJECT_REPOS` stops modules from adding their own repositories. | Android project, plugins |
| [`build.gradle.kts`](../build.gradle.kts) | Plugin aliases with `apply false` | Resolves the AGP, Compose, Serialization and KSP plugins once for the build. `org.jetbrains.kotlin.android` and kapt are not applied. | AGP and Kotlin plugins |
| [`gradle/libs.versions.toml`](../gradle/libs.versions.toml) | `[versions]`, `[libraries]`, `[plugins]` | Single source of truth for every pinned version. | Version catalog, all dependency tasks |
| [`gradle/wrapper/`](../gradle/wrapper/), [`gradlew`](../gradlew), [`gradlew.bat`](../gradlew.bat) | Gradle 9.5.0 wrapper | Downloads Gradle 9.5.0 and checks it against `distributionSha256Sum`. The jar now matches the official 9.5.0 wrapper jar. **Modified in this run.** | Gradle wrapper |
| [`.gitattributes`](../.gitattributes) | Line-ending rules | Keeps `gradlew` as LF and `*.bat` as CRLF, and marks `*.jar` as binary, so the scripts run after checkout on any OS. **Created in this run.** | Reproducible wrapper |
| [`gradle/gradle-daemon-jvm.properties`](../gradle/gradle-daemon-jvm.properties) | `toolchainVersion=17` | Makes Gradle run its daemon on a JDK 17. | JDK 17 |
| [`app/build.gradle.kts`](../app/build.gradle.kts) | `android {}`, `kotlin { compilerOptions }`, `ksp {}`, `buildSetting()` | Sets compileSdk 37, targetSdk 36, minSdk 24 and Java/JVM target 17. Adds the dependencies, and points KSP at the Room schema folder. Resolves the backend URL into `BuildConfig.API_BASE_URL`. Fails release builds that lack an HTTPS backend URL. | Project, dependencies, backend configuration |
| [`core/network/NetworkConfig.kt`](../app/src/main/java/com/campusmeal/android/core/network/NetworkConfig.kt) | `NetworkConfig` | Holds the CampusMeal API base URL, the logging flag and timeouts. Rejects a blank base URL or one that doesn't end in `/`. | Network boundary |
| [`core/network/ApiClientFactory.kt`](../app/src/main/java/com/campusmeal/android/core/network/ApiClientFactory.kt) | `createOkHttpClient()`, `createRetrofit()`, `SENSITIVE_HEADERS`, `json` | The only place OkHttp and Retrofit are created. Retrofit uses the kotlinx-serialization converter (unknown fields are ignored). The logging interceptor is only added in debug, at header level, with sensitive headers redacted. | Network boundary, log safety |
| [`core/network/ApiResult.kt`](../app/src/main/java/com/campusmeal/android/core/network/ApiResult.kt) | `ApiResult`, `apiCall {}` | Wraps a Retrofit suspend call. Maps HTTP 401 to `Unauthorized`, other error statuses to `HttpError(code)`, `IOException` to `NetworkUnavailable` and decoding errors to `InvalidResponse`. Error bodies are dropped. Cancellation and unexpected exceptions still propagate. **Created in this run.** | Separates remote sources from repositories |
| [`core/database/CampusMealDatabase.kt`](../app/src/main/java/com/campusmeal/android/core/database/CampusMealDatabase.kt) | `CampusMealDatabase`, `create(context)` | Room database, version 1, with schema export on. No seed data. Feature entities are added to `entities` with their features. | Database boundary |
| [`core/database/CacheMetadata.kt`](../app/src/main/java/com/campusmeal/android/core/database/CacheMetadata.kt) | `CacheMetadataEntity`, `CacheMetadataDao` | Records when each cached data set was last synced, so repositories can show cached data while offline. Room also needs at least one entity to compile. | Future feature caches |
| [`app/schemas/.../1.json`](../app/schemas/com.campusmeal.android.core.database.CampusMealDatabase/1.json) | Exported schema | Reviewable schema history for future migrations. | Room with KSP |
| [`core/datastore/CampusMealPreferences.kt`](../app/src/main/java/com/campusmeal/android/core/datastore/CampusMealPreferences.kt) | `CampusMealPreferences.dataStore(context)` | The single Preferences DataStore instance, for non-sensitive settings only. | DataStore boundary |
| [`core/session/SessionStorage.kt`](../app/src/main/java/com/campusmeal/android/core/session/SessionStorage.kt) | `SessionStorage`, `InMemorySessionStorage`, `SessionTokens` | The only place tokens may be stored. The interim implementation keeps them in memory only. `SessionTokens.toString()` hides the token values. | Session boundary, future Keystore storage |
| [`core/session/SessionRepository.kt`](../app/src/main/java/com/campusmeal/android/core/session/SessionRepository.kt) | `SessionRepository.isAuthenticated`, `signOut()` | Example of the repository pattern: it receives a `SessionStorage` interface through its constructor and gives consumers session state without exposing raw tokens. | Repository boundary |
| [`test/.../ApiClientFactoryTest.kt`](../app/src/test/java/com/campusmeal/android/core/network/ApiClientFactoryTest.kt) | 3 JVM tests | With MockWebServer: no logging interceptor when logging is off; header values and bodies never appear in debug logs; Retrofit uses the configured base URL. | Infrastructure tests |
| [`test/.../ApiResultTest.kt`](../app/src/test/java/com/campusmeal/android/core/network/ApiResultTest.kt) | 5 JVM tests | With MockWebServer: success (unknown fields ignored), 401, 503 (status code only), unreachable server, undecodable body. **Created in this run.** | Infrastructure tests |
| [`androidTest/.../CampusMealDatabaseTest.kt`](../app/src/androidTest/java/com/campusmeal/android/core/database/CampusMealDatabaseTest.kt) | 1 instrumented test | Runs the DAO against an in-memory Room database: upsert replaces the row, `clear()` removes it. **Created in this run.** | Infrastructure tests |
| [`README.md`](../README.md) | Requirements, build and test commands | Setup and command reference. **Modified in this run:** SDK location for command-line builds, `ApiResult` in the structure, instrumented Room test, link to `docs/`. | Documented commands |

## How the foundation works

1. **Gradle configuration.**
   - `settings.gradle.kts` sets the repositories.
   - `libs.versions.toml` pins every version.
   - `app/build.gradle.kts` applies AGP with built-in Kotlin plus the Compose, Serialization and KSP plugins. KSP generates the Room code and exports the schema.
   - The backend URL is read from an environment variable, a Gradle property or `local.properties`, in that order. It is written into `BuildConfig.API_BASE_URL`. Debug builds fall back to the emulator address `http://10.0.2.2:3000/api/v1/`.
2. **Network client creation.**
   - `DefaultAppContainer` builds a `NetworkConfig` from `BuildConfig`, with logging on only in debug builds.
   - `ApiClientFactory` then creates one shared `OkHttpClient` and one `Retrofit` instance, both created on first use.
3. **Persistence access.**
   - The container also creates `CampusMealDatabase`, the Preferences DataStore and `SessionStorage` on first use.
   - Room and DataStore hold non-sensitive cache and preference data only. Tokens go through `SessionStorage` only.
4. **Future repositories.** A feature issue adds:
   - a Retrofit service interface;
   - a repository interface and its implementation, which receive the service and a DAO through the constructor;
   - wiring in `AppContainer`.

   The implementation wraps remote calls in `apiCall {}`. It uses `CacheMetadataDao` to decide when cached data is stale. It turns `ApiResult` into the shared `UiState`, for example `NetworkUnavailable` plus cached data becomes `OfflineWithCache`, and `Unauthorized` becomes `Unauthorized`. Tests replace the service with MockWebServer or a fake, and the DAO with an in-memory database. No generic `BaseRepository` is provided on purpose.

## Security decisions

- **Secrets:**
  - No API keys, JWTs, refresh tokens or passwords are tracked; this was checked in the working tree and across the full git history.
  - `local.properties`, `.gradle/`, `.idea/` and keystores are gitignored.
  - Backend URLs are not secrets, and the release URL comes only from the environment or local properties.
- **Network logs:**
  - Only debug builds install `HttpLoggingInterceptor`, at `HEADERS` level. `Authorization`, `Proxy-Authorization`, `Cookie`, `Set-Cookie` and `X-Refresh-Token` are redacted.
  - Bodies are never logged, and `ApiResult.HttpError` drops error bodies.
  - Release builds have no logging interceptor. `ApiClientFactoryTest` checks both cases.
- **Session storage:**
  - Tokens may only go through `SessionStorage`. `InMemorySessionStorage` never writes them to disk.
  - Room and DataStore are plaintext and must not store tokens.
  - A Keystore-backed implementation is still pending and belongs to a later authentication issue.
- **External route provider:**
  - The app only calls the CampusMeal NestJS API (Android → NestJS → route provider).
  - The Android project contains no route-provider URLs, SDKs or credentials, so those credentials stay in NestJS.
  - Debug cleartext HTTP is limited to `10.0.2.2`, `localhost` and `127.0.0.1`.
- **Location:** only `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION` are declared. `ACCESS_BACKGROUND_LOCATION` is absent.
- **Excluded libraries:** Firebase Authentication and Hilt are absent.

## Build and verification

Run on 2026-09-15 from `android/` on Windows 11, with no environment overrides:
- `JAVA_HOME` points to JDK 21, but Gradle picks JDK 17 at `~/.jdks/temurin-17.0.20.1` for the daemon.
- The Android SDK location comes from the untracked `local.properties`.
- The SDK has the `android-37.0` platform.

| Command | Result |
| --- | --- |
| `./gradlew --version` | Exit 0. Gradle 9.5.0; daemon JVM "Compatible with Java 17" from `gradle/gradle-daemon-jvm.properties`. |
| `./gradlew wrapper --gradle-version 9.5.0 --distribution-type bin --gradle-distribution-sha256-sum 553c78f5…b746` | BUILD SUCCESSFUL. Jar SHA-256 `497c8c2a…a9c7` matches the official `gradle-9.5.0-wrapper.jar.sha256`. The previous jar matched the 9.0.0 wrapper. |
| `./gradlew :app:assembleDebug` | BUILD SUCCESSFUL. |
| `./gradlew :app:testDebugUnitTest` | BUILD SUCCESSFUL. 11 tests, 0 failures, 0 skipped: `FoundationSmokeTest` 3, `ApiClientFactoryTest` 3, `ApiResultTest` 5. |
| `./gradlew :app:lintDebug` | BUILD SUCCESSFUL. 0 errors, 6 warnings. Five report newer versions than the pinned ones and one is `OldTargetApi` for the pinned targetSdk 36. All are intentional. |
| `./gradlew :app:assembleDebugAndroidTest` | BUILD SUCCESSFUL. |
| `./gradlew :app:connectedDebugAndroidTest` | BUILD SUCCESSFUL on the `Tusky_API_36` emulator (API 36). 2 tests, 0 failures: `CampusMealAppTest.appShellShowsFoundationPlaceholder` and `CampusMealDatabaseTest.upsertReplacesCacheMetadataAndClearRemovesIt`. |

Static checks:
- Gradle files and the catalog contain no `org.jetbrains.kotlin.android`, `kotlin-kapt` or `kapt(...)`. The word "kapt" appears only in a catalog comment saying it is excluded.
- Room uses `ksp(libs.androidx.room.compiler)`.
- No Compose library entry declares its own version.
- No Firebase, Hilt or `ACCESS_BACKGROUND_LOCATION`.
- No secrets were found in the working tree or the git history.

## Coordination with Person B

- **No changes to Natalia's files in this run:** `MainActivity`, `CampusMealApp`, the theme, navigation, `UiState`, `AppContainer`, `AndroidManifest.xml`, `FoundationSmokeTest` and `CampusMealAppTest`.
- **Git authorship:** `4ac5079` is the only commit touching `android/` and is authored by Juan Pablo Bedoya. It also includes the Person B files listed above, so git history alone cannot separate the two contributions. Natalia should review those files, and adopt or adjust them, as her part of issue #1.
- **`AppContainer`:**
  - It already exposes what the infrastructure needs: `networkConfig`, `okHttpClient`, `retrofit`, `database`, `preferences`, `sessionStorage` and `sessionRepository`.
  - Keep these properties, and keep `NetworkConfig` built from `BuildConfig.API_BASE_URL` and `BuildConfig.DEBUG`. The release-logging guarantee depends on that.
  - Wire new repositories here and pass them to ViewModels through constructors.
- **`UiState`:** when Natalia's `UiState` changes, check that `ApiResult` still maps onto it: `NetworkUnavailable` to `OfflineWithCache` or `Error`, `Unauthorized` to `Unauthorized`.
- **Manifest (`4ac5079`):** Natalia owns the permissions. Please review the network security config, `allowBackup="false"` and the backup and data-extraction rules together with them.
- **README:** the requirements, project structure and build/test sections were edited in this run. Please review them.
- **`androidTest`:** it now contains `CampusMealDatabaseTest` next to `CampusMealAppTest`. `connectedDebugAndroidTest` runs both.
- **Clean-checkout validation:** `gradlew` is now executable and has LF line endings. Validating from a clean checkout on another machine is still Natalia's task.

## Remaining work

- **Not blocking Person A's part:**
  - Commit and push need the team's authorization. The executable bit of `gradlew` is currently staged in the git index.
  - The issue #1 comment has to be posted manually; the GitHub CLI is not installed on this machine.
- **Person B tasks still open:** review of the app shell files, visual validation from a clean checkout, and closing issue #1 after both reviews.
- **Later issues, out of scope for issue #1:**
  - Keystore-backed `SessionStorage`, the authorization interceptor and token refresh.
  - Feature Retrofit services, data models, repositories, Room entities and migrations.
  - WorkManager sync jobs and Coil image loader configuration.
  - Release signing, R8 rules and the production backend URL.
