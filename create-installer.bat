@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot
set JAVAFX_JMODS=Z:\Java Library\zulu21.36.17-ca-fx-jdk21.0.4-win_x64\jmods
set WIX_DIR=C:\Program Files (x86)\WiX Toolset v3.11\bin

REM Clean and prepare directories
rmdir /S /Q target\installer 2>nul
rmdir /S /Q target\app 2>nul
mkdir target\app

REM Copy application files
copy target\Mechiu-v2-1.0.0.jar target\app\
xcopy /E /I /Y src\main\resources target\app
xcopy /E /I /Y data\*.* target\app\data\

REM Create application bundle first using jpackage
jpackage ^
--type app-image ^
--name "Mechiu Typer Game" ^
--input target\app ^
--main-jar Mechiu-v2-1.0.0.jar ^
--main-class keltiga.MechiuApp ^
--icon src\main\resources\icon.ico ^
--dest target\bundle ^
--add-modules javafx.controls,javafx.fxml,javafx.media ^
--module-path "%JAVAFX_JMODS%" 

REM Generate WiX source file
"%WIX_DIR%\heat.exe" dir target\bundle\Mechiu Typer Game ^
-out target\installer\MechiuFiles.wxs ^
-ag -scom -sfrag -srd -dr INSTALLDIR ^
-cg MechiuComponents -var var.SourceDir

REM Create main WiX file
echo ^<?xml version="1.0" encoding="UTF-8"?^> > target\installer\MechiuInstaller.wxs
echo ^<Wix xmlns="http://schemas.microsoft.com/wix/2006/wi"^> >> target\installer\MechiuInstaller.wxs
echo   ^<Product Id="*" Name="Mechiu Typer Game" Version="1.0.0" Manufacturer="Zaidan Ahmad" UpgradeCode="PUT-GUID-HERE" Language="1033"^> >> target\installer\MechiuInstaller.wxs
echo     ^<Package InstallerVersion="200" Compressed="yes" InstallScope="perMachine" /^> >> target\installer\MechiuInstaller.wxs
echo     ^<Media Id="1" Cabinet="mechiu.cab" EmbedCab="yes" /^> >> target\installer\MechiuInstaller.wxs
echo     ^<Directory Id="TARGETDIR" Name="SourceDir"^> >> target\installer\MechiuInstaller.wxs
echo       ^<Directory Id="ProgramFilesFolder"^> >> target\installer\MechiuInstaller.wxs
echo         ^<Directory Id="INSTALLDIR" Name="Mechiu Typer Game"^> >> target\installer\MechiuInstaller.wxs
echo           ^<Directory Id="DATADIR" Name="data" /^> >> target\installer\MechiuInstaller.wxs
echo         ^</Directory^> >> target\installer\MechiuInstaller.wxs
echo       ^</Directory^> >> target\installer\MechiuInstaller.wxs
echo     ^</Directory^> >> target\installer\MechiuInstaller.wxs
echo     ^<Feature Id="ProductFeature" Title="Mechiu Typer Game" Level="1"^> >> target\installer\MechiuInstaller.wxs
echo       ^<ComponentGroupRef Id="MechiuComponents" /^> >> target\installer\MechiuInstaller.wxs
echo     ^</Feature^> >> target\installer\MechiuInstaller.wxs
echo   ^</Product^> >> target\installer\MechiuInstaller.wxs
echo ^</Wix^> >> target\installer\MechiuInstaller.wxs

REM Compile and link
"%WIX_DIR%\candle.exe" -dSourceDir="target\bundle\Mechiu Typer Game" target\installer\MechiuInstaller.wxs target\installer\MechiuFiles.wxs -out target\installer\
"%WIX_DIR%\light.exe" -out "target\installer\MechiuTyperGame.msi" target\installer\MechiuInstaller.wixobj target\installer\MechiuFiles.wixobj

echo Installer creation completed!
pause