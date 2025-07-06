# GitDesk
A Git Desktop application produced as part of my A-level coursework, which contributed to my achievement of an A* in Computer Science. 

## Features
The main idea of GitDesk was to provide a desktop application for Git project management. This would rival similar applications such as GitHub Desktop. The main features included:

- A main menu, allowing user to open a previous repository, select one from disk, or clone one from the web
- A text editor, complete with syntax highlighting for popular languages
- User-friendly forms/buttons for Git features, including:
  - Checkout
  - Commit
  - Pull/push
  - Merge
- Integration with GitHub API for issues and pull requests
- Integration with a custom [web backend](https://github.com/Harleyoc1/GitDesk-Backend) for a unique checklist feature

## Environment
GitDesk was written using JavaFX, with logic implemented in Kotlin and layouts in FXML. Gradle was used as the build tool. Dependency versions:

- Gradle 7.3
- Java 17 with Kotlin 1.6.0
- JavaFX 17
- Log4j 2.15.0
- JUnit 5.8.2
- [JavaUtilities](https://github.com/Harleyoc1/JavaUtilities) 0.1.2
- Moshi 1.13.0

The project was split into four key modules: 

- `util`: miscellaneous utility classes/functions
- `git`: handled interactions with Git via the command line
- `data`: handled internal data storage and interactions with GitHub and GitDesk APIs
- `ui`: UI logic and layouts
