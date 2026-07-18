@echo off
setlocal
cd /d "%~dp0"

echo Compiling...
del /q sources.txt 2>nul
(for /r %%f in (*.java) do @echo %%f) > sources.txt
javac -d . @sources.txt
if errorlevel 1 (
    echo.
    echo Compilation failed.
    del /q sources.txt
    pause
    exit /b 1
)
del /q sources.txt

echo Launching...
start "" javaw -cp "." main
exit
