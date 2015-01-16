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

#-------------------------------
# COSBENCH DRIVER STOPPER
#-------------------------------

checkAll(){
	cat ip-port.list |while read line
	do
		echo $line
		port=`echo $line |awk -F ":" 'NR==1{print $NF}'`
		let "x=1+$port"	
		OSGI_CONSOLE_PORT=$x
		echo $port
        	SERVICE_NAME=driver                                           
        	sh cosbench-stop.sh $SERVICE_NAME $OSGI_CONSOLE_PORT
	done	
}

checkOne(){
	s=$(awk 'NR=='$1' {print $0}' ip-port.list)
	port=`echo $s |awk -F ":" 'NR==1{print $NF}'`
	let "x=1+$port"
	OSGI_CONSOLE_PORT=$x
	echo $port
	SERVICE_NAME=driver                                           
	sh cosbench-stop.sh $SERVICE_NAME $OSGI_CONSOLE_PORT
}

if [ $# -eq 0 ];then
	checkAll
elif [ $# -eq 1 ];then
	checkOne $1
else
	echo "		<All>:none parameter, stop all"
	echo "		<one>:one parameter,the serial number in ip-port.list "	
	exit 1
fi








