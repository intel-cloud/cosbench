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

# clean sysstat output
cat NodeList | while read node 
do 
	#skip blank line
	if [ -z "$node" ]; then continue; fi
	# if the first letter is not "#"
	if [ ${node:0:1} = '#' ]; then continue; fi

	echo "clean up data on $node..."

	$SSHCMD $node "rm -rf $OUTPUTDIR/*.log"
	$SSHCMD $node "rm -rf $OUTPUTDIR/*.csv"
done
