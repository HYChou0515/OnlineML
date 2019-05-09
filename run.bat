@echo off
git submodule update --init
for /F "tokens=*" %%a in ('dir /b service') do (
	cd service\%%a
	start gradle clean bootRun
	cd ..\..
)