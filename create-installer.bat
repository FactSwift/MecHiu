@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot
set JAVAFX_JMODS=Z:\Java Library\zulu21.36.17-ca-fx-jdk21.0.4-win_x64\jmods

REM Clean and prepare directories
rmdir /S /Q target\installer 2>nul
rmdir /S /Q target\app 2>nul
mkdir target\app

REM Copy application files
copy target\Mechiu-v2-1.0.0.jar target\app\
xcopy /E /I /Y src\main\resources target\app
xcopy /E /I /Y data\*.json target\app

REM Create installer
jpackage ^
--type exe ^
--name "Mechiu Typer Game" ^
--app-version 1.0.0 ^
--input target\app ^
--main-jar Mechiu-v2-1.0.0.jar ^
--main-class keltiga.MechiuApp ^
--icon src\main\resources\icon.ico ^
--dest target\installer ^
--vendor "Zaidan Ahmad" ^
--win-dir-chooser ^
--win-shortcut ^
--win-menu ^
--add-modules javafx.controls,javafx.fxml,javafx.media ^
--module-path "%JAVAFX_JMODS%" ^
--verbose

echo Installer creation completed!
pause