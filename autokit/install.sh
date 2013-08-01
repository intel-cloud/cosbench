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

# remote install
cat NodeList | while read node 
do 
	#skip blank line
	if [ -z "$node" ]; then continue; fi
	# if the first letter is not "#"
	if [ ${node:0:1} = '#' ]; then continue; fi

	echo "copying scripts to $node..." 

	$SSHCMD $node "rm -rf $INSTALLDIR" 
	$SSHCMD $node "mkdir -p $INSTALLDIR" 
	$SCPCMD -r * $node:$INSTALLDIR/
	$SSHCMD $node "rm -rf $OUTPUTDIR"
	$SSHCMD $node "mkdir -p /temp"
	$SSHCMD $node "mkdir -p $OUTPUTDIR"
done

# local install
rm -rf $RESULTRT
mkdir -p $RESULTRT
