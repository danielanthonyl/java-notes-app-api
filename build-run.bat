@echo off
setLocal enabledelayedexpansion
cls

@REM process src files
set "srcPath=.\com\notesapp"

set "files="
set "fileCount=0"
set "comma="
set "parentDirectory=src"

@REM cleanup old builds
for /r %srcPath% %%F in (*.class) do (
    del %%F
)

for /r %srcPath% %%F in (*.java) do (
    set "relativePath=%%F"
    set "relativePath=!relativePath:%CD%\=!"

    echo processing file: !relativePath!

    set "files=!files! !relativePath!"
)

@REM process libraries

set "libsPath=.\lib"
set "libsIndex=0"
set "separator="
set "libraries="

for %%F in (%libsPath%\*.jar) do (
    if not !libsIndex! equ 0 (
        set "separator=;"
    )

    set /a libsIndex+=1
    echo processing lib: %%~nxF
    set "libraries=!libraries!!separator!!libsPath!\%%~nxF"
)

set "libraries=!libraries!;."

javac -cp %libraries% %files%

java -cp %libraries% com.notesapp.src.Main

