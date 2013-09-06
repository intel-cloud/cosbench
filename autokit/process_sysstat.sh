#!/bin/bash

source ./header.sh

#-------------------------------------------
# Main 
#-------------------------------------------

if [ ! -f NodeList ]; then 
	echo "No NodeList file";
	exit;
fi

# Post-processing
cat NodeList | while read node 
do 
	#skip blank line
	if [ -z "$node" ]; then continue; fi
	# if the first letter is not "#"
	if [ ${node:0:1} = '#' ]; then continue; fi

	echo "post-processing sar/iostat/vmstat data on $node..."

	$SSHCMD $node "sadf -p -T -- -bBrwqWuR -P ALL -n DEV $INTERVAL 0 $OUTPUTDIR/sar_raw.log > $OUTPUTDIR/sar.log"
	sleep 2
	$SSHCMD $node perl $INSTALLDIR/sar2csv.pl $OUTPUTDIR/sar.log $OUTPUTDIR/sar.csv  
	$SSHCMD $node perl $INSTALLDIR/iostat2csv.pl $OUTPUTDIR/iostat.log $OUTPUTDIR/iostat.csv  
	$SSHCMD $node perl $INSTALLDIR/vmstat2csv.pl $OUTPUTDIR/vmstat.log $OUTPUTDIR/vmstat.csv  
	sleep 2
	$SSHCMD $node perl $INSTALLDIR/csv2avg.pl $OUTPUTDIR/sar.csv $OUTPUTDIR/sar_avg.csv  
	$SSHCMD $node perl $INSTALLDIR/csv2avg.pl $OUTPUTDIR/iostat.csv $OUTPUTDIR/iostat_avg.csv  
	$SSHCMD $node perl $INSTALLDIR/csv2avg.pl $OUTPUTDIR/vmstat.csv $OUTPUTDIR/vmstat_avg.csv  
	sleep 2
	# merge vmstat.csv, iostat.csv and sar.csv to sysstat.csv for display in excel
	$SSHCMD $node "paste -d ',' $OUTPUTDIR/vmstat.csv $OUTPUTDIR/iostat.csv $OUTPUTDIR/sar.csv > $OUTPUTDIR/sysstat.csv"
	$SSHCMD $node "cat $OUTPUTDIR/vmstat_avg.csv $OUTPUTDIR/iostat_avg.csv $OUTPUTDIR/sar_avg.csv > $OUTPUTDIR/sysstat_avg.csv"	
done

