@echo off

cls

set "packagePath=.\com\notesapp\src"
set "libraries=.\lib\jackson-core-2.16.0.jar;.\lib\jackson-databind-2.16.0.jar;.\lib\jackson-annotations-2.16.0.jar;."

javac -cp ^
    %libraries% ^
    %packagePath%\Notes.java ^
    %packagePath%\Server.java ^
    %packagePath%\Main.java

java -cp ^
    %libraries% ^
    com.notesapp.src.Main