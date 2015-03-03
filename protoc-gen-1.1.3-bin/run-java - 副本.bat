@echo off&setlocal enabledelayedexpansion
set mulu=F:/workspace/tool_protobuff/src_proto/com/proto
set project_path=F:/workspace/tool_protobuff
set bin_path=F:/workspace/tool_protobuff/output_java/
cls
for /r %mulu% %%i in (*.proto) do (
set f=%%~dpi%%~nxi
echo 开始生成!f!
protoc.exe --java_out=!bin_path! --proto_path=!f!
)

pause