# README – "AthliTrack" Gym Tracker

## Overview

Competitor: *Bryan Ciaponi*
Technology: *Java 21 + JavaFX with FXML views (frontend)*
IDE: *Visual Studio Code / IntelliJ IDEA*
Database: *SQLite (file-based, no server needed — accessed via bundled JDBC driver)*

The application is a self-contained fat jar: JavaFX, the SQLite driver and
the JSON library are all bundled inside `AthliTrack.jar`, so no internet,
no Maven and no database server are required to run it.

Project structure (`source/`, standard Maven layout):

```text
source/
  pom.xml                      Maven build (mvn package, optional)
  build.bat                    Build without Maven (JDK 17+, uses lib/)
  lib/                         Bundled libraries (JavaFX, SQLite, Gson)
  src/main/java/athlitrack/
    Main.java                  Entry point (launcher)
    App.java                   Main window + FXML navigation
    Database.java              SQLite data layer (all SQL lives here)
    DbException.java           Unchecked database error wrapper
    UiHelpers.java             Dialogs, formatting, validation helpers
    model/                     Plain data classes (Exercise, Workout, ...)
    controller/                One controller per FXML view (UI behaviour)
  src/main/resources/
    exercises.json             Initial exercise catalogue (auto-imported)
    athlitrack/view/*.fxml     All 9 windows/dialogs (Scene Builder compatible)
    athlitrack/view/app.css    Shared stylesheet
```

## Installation

No installation is needed.

1. Copy the delivered archive anywhere and extract it.
2. Make sure **Java 17 or newer** is installed (check with `java -version`).
   Any recent JDK/JRE works; JavaFX does NOT need to be installed
   separately because it is bundled in the jar.
3. Nothing else to install: there are no external dependencies.

The database (`athlitrack.db`) is created automatically next to the program
on first start, the tables are created from `database/schema.sql` logic and
the 20 exercises from the bundled `exercises.json` are imported automatically.

## Execution

1. Navigate to the directory `executable` found within the delivered archive.
2. Double-click **`AthliTrack.bat`** (it simply runs `java -jar AthliTrack.jar`).
3. Alternative (command line): `java -jar AthliTrack.jar` inside the
   `executable` folder. Double-clicking `AthliTrack.jar` directly works too
   when `.jar` files are associated with Java.

To rebuild from source: open a terminal in the `source` folder (JDK 17+
required) and run `build.bat`. It compiles with `javac` and re-creates
`AthliTrack.jar` — no Maven and no downloads needed, libraries are in `lib/`.
With Maven installed, `mvn package` produces the same result. The FXML files
can be opened and edited visually with Gluon Scene Builder.

### Run from IntelliJ IDEA

1. Open the `source` folder (File → Open → select the `source` folder).
   IntelliJ detects `pom.xml` and imports the Maven module `athlitrack`
   (accept "Trust Project" and wait for the import/Maven download to finish).
2. The project SDK is preconfigured (`source/.idea/misc.xml` points to the
   JDK `21`). If you still see `java: JDK isn't specified for module
   'athlitrack'`, set it once: File → Project Structure → Project → SDK →
   pick JDK 17 or newer (Add SDK → Add JDK → e.g.
   `C:\Program Files\Microsoft\jdk-21.0.7.6-hotspot` if the list is empty),
   Language Level 17+, Apply → OK.
3. A ready-made run configuration `AthliTrack` (main class
   `athlitrack.Main`) is included: just press the green Run arrow or
   Shift+F10. No VM options or extra setup needed.

### Troubleshooting (IntelliJ IDEA)

Error `java: JDK isn't specified for module 'athlitrack'` means the IDE has
no JDK assigned to the project — follow step 2 above. The JDK must be
registered under File → Project Structure → Platform Settings → SDKs; the
project itself already points to the JDK named `21`.

No Maven/IDE setup is needed to *run* the app: just double-click
`executable/AthliTrack.bat`.

## Comments (optional)

- Date fields use a date picker plus a time field in format `HH:mm`
  (e.g. `18:30`). A `Now + 1h` button pre-fills start/end with the current time.
- "Sets can be deleted by swiping left" (mobile gesture from the task) is
  implemented on desktop as a **Delete set** button next to the sets table,
  plus inline editing by clicking a set row.
- When adding a workout, a modal asks to pick a template for pre-filling
  (sets/reps from the template, weight starts at 0 to be entered manually)
  or to start empty. If no templates exist, an appropriate message is shown.
- The progress line chart (JavaFX `LineChart`) shows the last 3 months plus
  the next expected value as the final point, while the list and the
  expected-value calculation use all-time data.
- Expected volume with a single record equals that record (no trend yet).
- Deleting an exercise that is used in a template or workout is blocked with
  an explanatory message (to protect existing data integrity).
- Assumption: the SQLite file lives in the working directory
  (`athlitrack.db`), so workouts persist between restarts on the same machine.

## Feedack (optional)

Thank you for the interesting task!
