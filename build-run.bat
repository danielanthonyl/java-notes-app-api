@echo off
setLocal enabledelayedexpansion
cls

@REM process src files
set "srcPath=.\com\notesapp"

set "files="
set "fileCount=0"
set "comma="
set "parentDirectory=src"

for /r %srcPath% %%F in (*.java) do (
    for %%a in (%%F) do for %%b in ("%%~dpa\.") do (
        if %%~nxb equ !parentDirectory! (
            set "parentDirectory=%%~nxb"
        ) else (
            set "parentDirectory=!parentDirectory!\%%~nxb"
        )
    )

    echo processing file: !parentDirectory!\%%~nxF

    set "files=!files! !srcPath!\!parentDirectory!\%%~nxF"
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
