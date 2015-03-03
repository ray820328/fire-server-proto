@echo off&setlocal enabledelayedexpansion
set mulu=F:/workspace/sharing_data/src/com/proto
set proto_path=F:/workspace/sharing_data/src
set as3_out=F:/workspace/sharing_data/src
cls
for /r %mulu% %%i in (*.proto) do (
set f=%%~dpi%%~nxi
echo 开始生成!f!
protoc --proto_path=!proto_path! --plugin=protoc-gen-as3=protoc-gen-as3.bat --as3_out=!as3_out! !f!

)

echo 完毕.

pause
