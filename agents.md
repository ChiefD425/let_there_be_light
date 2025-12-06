# Agent Context for Light Controller App

## Versioning Strategy
This project follows **Semantic Versioning (SemVer)** (MAJOR.MINOR.PATCH):
- **MAJOR**: Incompatible API changes.
- **MINOR**: Backward-compatible functionality.
- **PATCH**: Backward-compatible bug fixes.

**Workflow Rules**:
1. **Continuous Documentation**: You must verify and update `README.md` and feature files (e.g., `architecture.md`) on *every* code change. Ensure documentation stays in sync with code.
2. **Update CHANGELOG.md**: Every feature or fix must be logged under the "Unreleased" section in `CHANGELOG.md`.
   - **Smart Editing**: Check for existing subsections (`### Added`, `### Changed`, `### Fixed`, etc.) under `[Unreleased]`.
   - **Merge Entries**: If the appropriate subsection exists, append your bullet point to it. Do NOT create duplicate headers.
   - **Create if Missing**: Only create a new subsection if it doesn't exist yet.
   - **Preserve History**: Keep all entries from the current day/session; do not overwrite them.
3. **Version Bump**: On release, bump the `versionName` and `versionCode` in `app/build.gradle.kts` and move "Unreleased" items to the new version header in `CHANGELOG.md`.

## Project Overview
This is an Android application for controlling addressable LEDs. The app allows users to manage multiple controllers, create custom light patterns, schedule operations, and sync devices.

## Technology Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (Material3)
- **Dependency Injection**: Hilt
- **Database**: Room
- **Concurrency**: Coroutines & Flow
- **Architecture**: Modern Android Architecture (MVVM + Clean Architecture principles)

## Architecture Patterns
The project, located in `com.antigravity.lights`, follows a separation of concerns:
- **`ui`**: Composables and ViewModels. Handles user interaction and state display.
- **`domain`**: Pure Kotlin business logic. Use Cases (Interactors) and Repository interfaces.
- **`data`**: Repository implementations, Room database, Network data sources (if any).
- **`di`**: Hilt modules providing dependencies.

## Coding Standards
- **Compose**: Use unidirectional data flow. Hoist state where possible.
- **Coroutines**: Use `suspend` functions and `Flow` for reactive data streams.
- **Style**: Follow official Kotlin coding conventions.
- **Documentation**: Keep KDoc concise.

## Key Files
- `MainActivity.kt`: Entry point.
- `LightControllerApp.kt`: Hilt Application class.
- `build.gradle.kts` (app): Dependencies (Compose BOM, Hilt, Room).

## Instructions for Agents
1. **Context Awareness**: Always verify the current state of architecture before introducing new patterns.
2. **State Management**: Use `StateFlow` in ViewModels and `collectAsStateWithLifecycle` in Composables.
3. **Refactoring**: When modifying the `domain` layer, ensure `data` and `ui` are updated accordingly.
4. **Testing**: Prefer testing ViewModels and Domain logic.
