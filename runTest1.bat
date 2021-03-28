copy "%~dp0\src\basicTest1.bluc" "%~dp0\dist\basicTest1.bluc"
java -jar "%~dp0\dist\BluC.jar" "%~dp0\dist\basicTest1.bluc" > "%~dp0\dist\err.txt"
start "" "%~dp0\dist\err.txt"