#!/bin/sh
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

cat NodeList | while read node 
do 
	#skip blank line
	if [ -z "$node" ]; then continue; fi
	# if the first letter is not "#"
	if [ ${node:0:1} = '#' ]; then continue; fi
	
	echo "Verifying $node..." 
	ping $node -c 1 > /dev/null 2>&1
	if [ $? = 0 ]; then 
		echo "ping $node ......................................... : Yes"; 
	else
		echo "ping $node ......................................... : No"; 
		continue	
	fi	

	$SSHCMD $node echo > /dev/null 2>&1
	if [ $? = 0 ]; then 
		echo "ssh $node .......................................... : Yes"; 
	else
		echo "ssh $node .......................................... : No"; 
		continue	
	fi	

	#Xuekun: FIXME, too ugly
	#$SSHCMD $node "ls $INSTALLDIR/sar2csv.pl" > tmp.txt 2>&1;  EPID=$!	
	#note that SSHCMD is "ssh -f"
	ssh $node "ls $INSTALLDIR/sar2csv.pl" > tmp.txt 2>&1 & EPID=$!	
	wait $EPID 	
	grep "ls" tmp.txt > /dev/null 2>&1
	if [ $? != 0 ]; then 
		echo "the scripts are installed in $node ................. : Yes"; 
	else
		echo "the scripts are installed in $node ................. : No"; 
	fi	
	rm -f tmp.txt

	#$SSHCMD $node vmstat > /dev/null 2>&1	
	ssh $node vmstat 1 1 > tmp.txt 2>&1 & EPID=$!	
	wait $EPID
	grep "command" tmp.txt > /dev/null 2>&1 
	if [ $? != 0 ]; then 
		echo "vmstat tool is installed in $node .................. : Yes"; 
	else
		echo "vmstat tool is installed in $node .................. : No"; 
	fi	
	rm -f tmp.txt
done
