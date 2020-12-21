@echo off

REM ===================================================================
REM Run the "GrepTrayManager" utility
REM ===================================================================
set base=C:\dev\utility\general\bin
set cp=%base%
set jopts=-Xms192m -Xmx384m -Xss24m -Dswing.aatext=true


REM ===================================================================
REM Display the command line and execute the silly application
REM ===================================================================
REM echo java -classpath "%cp%" %jopts% std.wlj.grep.GrepTrayManager

start /B java -classpath "%cp%" %jopts% std.wlj.grep.GrepTrayManager

