@ECHO on
CALL logInformation 'Initializing Appveyor Script'
CALL ./gradlew.bat clean buildPlugin check --stacktrace --debug
EXIT /B %ERRORLEVEL%
:logInformation
ECHO " "
ECHO "==================== %~1 ===================="
ECHO " "
EXIT /B 0
