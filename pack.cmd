:: 
:: Copyright 2013 Intel Corporation, All Rights Reserved.
:: 
:: Licensed under the Apache License, Version 2.0 (the "License");
:: you may not use this file except in compliance with the License.
:: You may obtain a copy of the License at
:: 
::    http://www.apache.org/licenses/LICENSE-2.0
:: 
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.
:: 

@echo off

if "%1_" == "_" (
	echo This script helps to generate one deliverable package.
	echo 
	echo Usage:
	echo         %0 {version}
	exit -1
)

echo "Build up main structure"
mkdir %1

xcopy /Y /E release\* %1\
xcopy /Y /E dist\* %1\

xcopy /Y LICENSE %1
xcopy /Y NOTICE %1
xcopy /Y VERSION %1

xcopy /Y README.md %1
xcopy /Y BUILD.md %1

xcopy /Y CHANGELOG %1
xcopy /Y TODO.md %1

xcopy /Y COSBenchUserGuide.pdf %1
xcopy /Y COSBenchAdaptorDevGuide.pdf %1
xcopy /Y 3rd-party-licenses.pdf %1
xcopy /Y pkg.lst %1

type nul>%1/BUILD.no
cscript /nologo datescript.vbs> %1/BUILD.no

echo "Build up adaptor example enviornment"
xcopy /Y /E ext\* %1\ext\

