@echo off
set relativeFileName=basicTest2.bluc

set relativeTestFolder=src
set fullTestFile=%~dp0%relativeTestFolder%\%relativeFileName%
set copyPath=%~dp0dist\%relativeFileName%
set errFile=%~dp0dist\err.txt

@echo on
copy "%fullTestFile%" "%copyPath%"
java -jar "%~dp0dist\BluC.jar" "%fullTestFile%" -parseTree -c -time > "%errFile%"

start "" "%errFile%"