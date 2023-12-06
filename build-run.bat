@echo off
setLocal enabledelayedexpansion
cls

set "srcPath=.\src\main\java\com\notesapp"
set "buildDirectory=.\build"
set "libsDirectory=.\lib"
set "files="

for /r %srcPath% %%F in (*.java) do (
    set "relativePath=%%F"
    set "relativePath=!relativePath:%CD%\=!"
    set "files=!files! !relativePath!"
)

javac -d %buildDirectory% -cp %libsDirectory%\* %files%
java -cp "%libsDirectory%\*;%buildDirectory%" com.notesapp.Main
