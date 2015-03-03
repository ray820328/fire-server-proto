@echo off&setlocal enabledelayedexpansion
set project=D:/ENV/eclipse/workspace/fire-server-proto/test/
set project_path=D:/ENV/eclipse/workspace/fire-server-proto/
set bin_path=D:/ENV/eclipse/workspace/fire-server-proto/output_java/
cls
for /r %project% %%i in (*.proto) do (
set f=%%~dpi%%~nxi
echo 开始生成!f!
protoc.exe --proto_path=%project% --java_out=%project% !f!
)

pause