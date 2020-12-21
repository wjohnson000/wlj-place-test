' This VB script will start the "run-grep.bat" file without opening
' a DOS (cmd) window.
'
' A chr(34) is the character equivalent to a double-quote, and could
' be replaced by two double-quotes.
'
' The ",0" at the end of the ".Run" command indicates the originating
' window will be closed and another window activated for the command. 

Set WshShell = CreateObject("WScript.Shell")
WshShell.Run chr(34) & "C:\dev\utility\general\src\grep\run-grep.bat" & chr(34), 0
Set WshShell = Nothing