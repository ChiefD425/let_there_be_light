# System Architecture

## Overview
The application follows the **Modern Android Architecture** guidelines, utilizing **MVVM** (Model-View-ViewModel) with a focus on Clean Architecture principles. This ensures scalability, testability, and a clear separation of concerns.

## Layers
The codebase is organized into three main layers:

### 1. UI Layer (`ui`)
- **Responsibility**: Displays data on the screen and handles user interactions.
- **Components**:
    - **Activities/Composables**: The view implementation using Jetpack Compose.
    - **ViewModels**: Holders of state and business logic for the UI. They expose `StateFlow` to the UI.

### 2. Domain Layer (`domain`)
- **Responsibility**: Encapsulates complex business logic and is purely Kotlin (no Android dependencies preferred).
- **Components**:
    - **Use Cases (Interactors)**: Single-responsibility classes that perform specific tasks.
    - **Repository Interfaces**: Definitions of data access contracts.

### 3. Data Layer (`data`)
- **Responsibility**: Manages application data (retrieving, saving, caching).
- **Components**:
    - **Repositories**: Implementations of the interfaces defined in the domain layer. They coordinate data from different sources.
    - **Data Sources**:
        - **Local**: Room Database (DAO, Entities).
        - **Remote**: Network calls (Retrofit/OkHttp) if applicable.

## Dependency Injection
**Hilt** is used for dependency injection to manage the lifecycle of components and handle dependency wiring.

## Architecture Diagrams

### High-Level Architecture
```mermaid
graph TD
    subgraph UI_Layer ["UI Layer"]
        UI[Composable Screens]
        VM[ViewModel]
    end

    subgraph Domain_Layer ["Domain Layer"]
        UC[Use Cases]
        RepoInt[Repository Interface]
    end

    subgraph Data_Layer ["Data Layer"]
        RepoImpl[Repository Implementation]
        Local[Local Data Source (Room)]
        Remote[Remote Data Source]
    end

    UI -->|Observes State / Sends Events| VM
    VM -->|Executes| UC
    UC -->|Uses| RepoInt
    RepoImpl ..|>|Implements| RepoInt
    RepoImpl -->|Reads/Writes| Local
    RepoImpl -->|Fetches| Remote
```

### Data Flow
1. **User Event**: User clicks a button in the UI.
2. **ViewModel**: Handles the event, executes a Use Case.
3. **Use Case**: Coordinates with the Repository to update/fetch data.
4. **Repository**: Updates the Local Database (Room).
5. **State Update**: Room emits new data via Flow.
6. **Observation**: ViewModel transforms data and updates `StateFlow`. UI recomposes to show the new state.

### Package Structure
```mermaid
classDiagram
    namespace com.antigravity.lights {
        class MainActivity
        class LightControllerApp
    }
    namespace ui {
        class Theme
        class Screens
        class ViewModels
    }
    namespace domain {
        class UseCases
        class RepositoryInterfaces
    }
    namespace data {
        class RepositoryImpl
        class RoomDatabase
    }
    namespace di {
        class AppModule
    }

    MainActivity --> ui
    ui --> domain
    domain ..> data : defined by
    data --|> domain : implements
    di ..> ui : injects
    di ..> domain : injects
    di ..> data : injects
```
