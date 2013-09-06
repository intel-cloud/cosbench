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

#start sysstat monitoring
cat NodeList | while read node 
do 
	#skip blank line
	if [ -z "$node" ]; then continue; fi
	# if the first letter is not "#"
	if [ ${node:0:1} = '#' ]; then continue; fi

	echo "collecting sar/iostat/vmstat on $node..."

	num_processors=`$SSHCMD $node cat /proc/cpuinfo | grep processor | wc -l`
	if [ $num_processors -eq 1 ]; then
	        $SSHCMD $node "sleep 90; sar -bBrwqWuR -o ${OUTPUTDIR}/sar_raw.log $INTERVAL 300> /dev/null &"
	else
		$SSHCMD $node "sleep 90; sar -bBrwqWuR -P ALL -n DEV -o ${OUTPUTDIR}/sar_raw.log $INTERVAL 300 > /dev/null &"
	fi

	$SSHCMD $node "sleep 90; iostat -d -k -t -x $INTERVAL 300 > ${OUTPUTDIR}/iostat.log &"
	$SSHCMD $node "sleep 90; vmstat -n $INTERVAL 300 > ${OUTPUTDIR}/vmstat.log &"
	$SSHCMD $node "sleep 90; mpstat -P ALL $INTERVAL 300 > ${OUTPUTDIR}/mpstat.log &"
done
