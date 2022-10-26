@echo off
set CORE_DIR=%~dp0..\..
set KOTLIN_CLASSPATH=%CORE_DIR%\build\classes\kotlin\main;%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib-jdk8\1.6.0\baf82c475e9372c25407f3d132439e4aa803b8b8\kotlin-stdlib-jdk8-1.6.0.jar;%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib-jdk7\1.6.0\da6bdc87391322974a43ccc00a25536ae74dad51\kotlin-stdlib-jdk7-1.6.0.jar;%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib\1.6.0\a40b8b22529b733892edf4b73468ce598bb17f04\kotlin-stdlib-1.6.0.jar;%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.jetbrains\annotations\13.0\919f0dfe192fb4e063e7dacadee7f8bb9a2672a9\annotations-13.0.jar;%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib-common\1.6.0\7857e365f925cfa060f941c1357cda1f8790502c\kotlin-stdlib-common-1.6.0.jar
java -classpath %KOTLIN_CLASSPATH% util.InteractiveRunnerKt ^
java -classpath %KOTLIN_CLASSPATH% r34.LocalTestingToolKt 1 -- ^
java -classpath %KOTLIN_CLASSPATH% r34.WinAsSecondKt
pause
