#!/bin/bash
release_id=`cat VERSION`'.'$(date +%Y%m%d)
echo "release id is: "$release_id
for dir in `ls dev`
do
  f='dev/'$dir"/META-INF/MANIFEST.MF"
  if [ -f $f ]	
	then	  	 
		sed -i "s/[0-9]\.[0-9]\.[0-9]\.[0-9]\{8\}/$release_id/g" $f
		echo "file: "$f" is updated"
	fi
done
for module in {driver,controller}
do
  web="dev/cosbench-"$module"-web/WEB-INF/freemarker/header.ftl"
  if [ -f $web ]
  then 
  	sed -i "s/[0-9]\.[0-9]\.[0-9]\.[0-9]\{8\}/$release_id/g" $web
	echo "file "$web " is updated"
  fi
done


		
