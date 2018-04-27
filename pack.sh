#!/bin/bash
#
#Copyright 2013 Intel Corporation, All Rights Reserved.
#
#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.
#

if [ $# -lt 1 ];
then
        echo Usage:  $0 {version}
        exit -1
fi

echo "Build up main structure"
mkdir $1

cp -f -R release/* $1/
cp -f -R dist/* $1/

cp -f LICENSE $1/
cp -f NOTICE $1/
cp -f VERSION $1/

cp -f README.md $1/
cp -f BUILD.md $1/

cp -f CHANGELOG $1/
cp -f TODO.md $1/

cp -f COSBenchUserGuide.pdf $1/
cp -f COSBenchAdaptorDevGuide.pdf $1/
cp -f 3rd-party-licenses.pdf $1/
cp -f pkg.lst $1/


echo $(date +%Y%m%d) > $1/BUILD.no

echo "Build up adaptor example enviornment"
mkdir $1/ext
cp -f -R ext/* $1/ext
