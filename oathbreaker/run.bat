@echo off
setlocal
set ROOT=%~dp0
set SRC=%ROOT%src\main\java
set OUT=%ROOT%target\classes

if not exist "%OUT%" mkdir "%OUT%"

dir /s /b "%SRC%\*.java" > "%TEMP%\nodequest-sources.txt"
javac -d "%OUT%" -encoding UTF-8 @"%TEMP%\nodequest-sources.txt"
if errorlevel 1 exit /b 1

java -cp "%OUT%" com.nodequest.Main
