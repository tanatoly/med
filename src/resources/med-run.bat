@echo off
set DIR=%~dp0
set PATH=%PATH%;%DIR%\lib
set JLINK_VM_OPTIONS=--add-exports javafx.controls/com.sun.javafx.scene.control.behavior=com.rafael.med --add-exports javafx.controls/com.sun.javafx.scene.control=com.rafael.med --add-exports javafx.base/com.sun.javafx.binding=com.rafael.med --add-exports javafx.graphics/com.sun.javafx.stage=com.rafael.med --add-exports javafx.base/com.sun.javafx.event=com.rafael.med --add-exports javafx.graphics/com.sun.javafx.scene.traversal=com.rafael.med --add-exports javafx.controls/com.sun.javafx.scene.control.inputmap=com.rafael.med --add-exports javafx.graphics/com.sun.javafx.scene.traversal=com.rafael.med --add-exports javafx.controls/com.sun.javafx.scene.control.inputmap=com.rafael.med --add-exports javafx.graphics/com.sun.javafx.util=com.rafael.med --add-exports javafx.graphics/com.sun.javafx.scene=com.rafael.med

start %DIR%\bin\javaw  %JLINK_VM_OPTIONS% -m com.rafael.med/com.rafael.med.MedStartup %*


