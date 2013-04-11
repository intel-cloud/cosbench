for /F %%i IN ('dir /s /b *.java') DO (
	grep "@author" %%i
	
)