#!/bin/bash

#-------------------------------------------
# Configurable Options
#-------------------------------------------

source ./header.sh

#-------------------------------------------
# Main
#-------------------------------------------

if [ ! -f NodeList ]; then 
	echo "No NodeList file";
	exit;
fi

mkdir -p $RESULTDIR
cp summary_v1.0.xlsm $RESULTDIR/

cat NodeList | while read node 
do 
	#skip blank line
	if [ -z "$node" ]; then continue; fi
	# if the first letter is not "#"
	if [ ${node:0:1} = '#' ]; then continue; fi

	mkdir -p $RESULTDIR/$node
	$SCPCMD $node:$OUTPUTDIR/*.csv $RESULTDIR/$node/
	$SCPCMD $node:$OUTPUTDIR/*.log $RESULTDIR/$node/
done

#$SCPCMD proxy:/emon/*.dat $RESULTDIR/192.168.0.200/
cp /power/SPECptd/*.csv $RESULTDIR
runid=$(cat /runid)
cp /power/SPECptd/power.csv  /powerdata/${runid}_power.csv


cd $RESULTDIR
tar -czf perf.tar.gz *
rm -f $RESULTRT/perf.tar.gz
mv perf.tar.gz $RESULTRT/perf.tar.gz

killall ptd-linux-x86
