#!/usr/bin/perl

$vmstatfile = "$ARGV[0]";
$CSVFILE = "$ARGV[1]";

# --------------------------------------------------------------------
# This reads through the entire IOSTAT log file, builds a list of unique
# device names (devicelist), a list of unique counter name (counterlist), 
# and builds a list of rawdata (rawdatalist)with device:::value.
# --------------------------------------------------------------------

open(VMSTATFILE, $vmstatfile) || die "Unable to open $vmstatfile!\n";
open(CSVFILE, ">$CSVFILE") || die "Unable to open $CSVFILE!\n";

$number = 0;
while(<VMSTATFILE>) {
	$number++;
	@datalist = split(" ", $_);
	if ($number == 1) { #print first line of vmstat 
		next
#		print CSVFILE $datalist[0];
#		print CSVFILE ",,,";
#		print CSVFILE $datalist[1];
#		print CSVFILE ",,,";
#		print CSVFILE $datalist[2];
#		print CSVFILE ",,";
#		print CSVFILE $datalist[3];
#		print CSVFILE ",,";
#		print CSVFILE $datalist[4];
#		print CSVFILE ",,";
#		print CSVFILE $datalist[5];
	}  
	else { 
#	        if ($_ =~ m/procs/) {
#	                next;
#	        }
#	        if ($_ =~ m/free/) {
#	                next;
#	        }
		for ($i = 0; $i <= $#datalist; $i++) {
			print CSVFILE $datalist[$i];
			print CSVFILE ",";
		}	
	}
	print CSVFILE "\n";
}
close(VMSTATFILE);
close(CSVFILE);
