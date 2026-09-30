@echo off
REM Script di build di AthliTrack (Windows).
REM Serve solo un JDK 17+ (javac + jar): controlla con "javac -version".
REM Niente Maven, niente internet: le librerie sono già nella cartella lib\.
REM Layout Maven standard: codice in src\main\java, FXML/CSS/dati in src\main\resources.
REM Produce AthliTrack.jar (fat jar autonomo) in questa cartella.

setlocal
cd /d "%~dp0"

if not exist build mkdir build
if not exist fat mkdir fat

echo [1/3] Compilazione...
javac -encoding UTF-8 -cp "lib\*" -d build src\main\java\athlitrack\*.java src\main\java\athlitrack\model\*.java src\main\java\athlitrack\controller\*.java
if %errorlevel% neq 0 (
    echo BUILD FALLITA - vedi errori di compilazione sopra
    exit /b 1
)

echo [2/3] Copia risorse...
xcopy /e /y src\main\resources\* build\ >nul

echo [3/3] Assemblaggio AthliTrack.jar...
rmdir /s /q fat
mkdir fat
xcopy /e /y build\* fat\ >nul
cd fat
for %%j in (..\lib\*.jar) do jar xf "%%j"
del /q META-INF\*.SF META-INF\*.RSA META-INF\*.DSA 2>nul
echo Main-Class: athlitrack.Main> manifest.txt
jar cfm ..\AthliTrack.jar manifest.txt .
cd ..
rmdir /s /q fat build

echo DONE: AthliTrack.jar
endlocal
