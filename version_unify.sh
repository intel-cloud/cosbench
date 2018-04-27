#!/bin/bash
release_id=`cat VERSION`
echo "release id is: "$release_id
for dir in `ls dev`
do
  f='dev/'$dir"/META-INF/MANIFEST.MF"
  if [ -f $f ]	
	then	  	 
		sed -i "s/Bundle-Version:.*$/Bundle-Version: ${release_id}/" $f
		echo "file: "$f" is updated"
	fi
done

