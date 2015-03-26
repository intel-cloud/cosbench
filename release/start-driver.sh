#!/bin/bash

ip=127.0.0.1 
num=1 
base_port=18088 

if [ $# -eq 0 ];then 
	num=1 
elif [ $# -eq 1 ]; then 
 	num=$1 
elif [ $# -eq 2 ]; then 
 	num=$1 
 	ip=$2 
elif [ $# -eq 3 ];then 
 	num=$1 
 	ip=$2 
 	base_port=$3 
else 
 	echo "<default>:none of parameter,create one driver" 
	echo "<mult>:pareameters" 
 	echo "	   	<1>the number of drivers on one node" 
	echo "	   	<2>the ip of driver" 
	echo "		<3>base of port" 
	exit 1 
fi 


SERVICE_NAME=driver
VERSION=`cat VERSION`
OSGI_BUNDLES="cosbench-log_${VERSION} cosbench-tomcat_${VERSION} cosbench-config_${VERSION} cosbench-http_${VERSION} cosbench-cdmi-util_${VERSION} cosbench-core_${VERSION} cosbench-core-web_${VERSION} cosbench-api_${VERSION} cosbench-mock_${VERSION} cosbench-ampli_${VERSION} cosbench-swift_${VERSION} cosbench-keystone_${VERSION} cosbench-httpauth_${VERSION} cosbench-s3_${VERSION} cosbench-librados_${VERSION} cosbench-scality_${VERSION} cosbench-cdmi-swift_${VERSION} cosbench-cdmi-base_${VERSION} cosbench-driver_${VERSION} cosbench-driver-web_${VERSION}"


rm -f ip-port.list
for i in $(seq 1 $num)
do
	# driver.conf
	name=""
	url=""
	cd conf
	let "x=($i-1)*100+$base_port"
	rm -f driver.conf
	rm -f driver_$i.conf
	cp driver_template.conf driver_$i.conf
	name="$ip:$x"
	url="http:\/\/$ip:$x\/driver"
	sed -i "s/^name=.*$/name=${name}/" driver_$i.conf
	sed -i "s/^url=.*$/url=${url}/" driver_$i.conf
	ln -s driver_$i.conf driver.conf

	#make driver-tomcat-server.xml
	rm -f driver-tomcat-server.xml
	rm -f driver-tomcat-server_$i.xml
	cp driver-tomcat-server_template.xml driver-tomcat-server_$i.xml
	sed -i "s/\(.*Connector port=\"\)[^\"]*\(.*\)/\1${x}\2/" driver-tomcat-server_$i.xml 
	let "x=$x+1"
	ln -s driver-tomcat-server_$i.xml driver-tomcat-server.xml
	cd ../
	OSGI_CONSOLE_PORT=$x
        sh cosbench-start.sh $SERVICE_NAME "$OSGI_BUNDLES" $OSGI_CONSOLE_PORT

	#ip/port list
	echo "$name" >> ip-port.list
done





