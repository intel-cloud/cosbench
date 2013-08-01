#!/usr/bin/perl

$iostatfile = "$ARGV[0]";
$CSVFILE = "$ARGV[1]";

print $iostatfile
# --------------------------------------------------------------------
# This reads through the entire IOSTAT log file, builds a list of unique
# device names (devicelist), a list of unique counter name (counterlist), 
# and builds a list of rawdata (rawdatalist)with device:::value.
# --------------------------------------------------------------------

open(IOSTATFILE, $iostatfile) || die "Unable to open $iostatfile!\n";

while(<IOSTATFILE>) {
	chomp;
	if ($_ =~ m/Linux/) {
		next;
	}
	if ($_ =~ m/Time/) {
		next;
	}
	# some version of iostat may print time as "06/10/2010 03:37:43 PM"
	if ($_ =~ m/AM/) {
		next;
	}
	if ($_ =~ m/PM/) {
		next;
	}
	if ($_ =~ /^$/) {
		next;
	}
	if ($_ =~ /Device/) {
		@counterlist = split(" ", $_);
	} else {
		@datalist = split(" ", $_);
		@found = grep /$datalist[0]/, @devicelist;
		if ($#found == -1 ) {
			push(@devicelist, $datalist[0]);
		}		
		foreach $i (1..$#datalist) {
			push (@rawdatalist, $datalist[$i]);
		}
      }
}
close(IOSTATFILE);

# --------------------------------------------------------------------
# Now merge all data into a single csv file
# --------------------------------------------------------------------

open(CSVFILE, ">$CSVFILE") || die "Unable to open $CSVFILE!\n";

# first line
foreach $device (@devicelist) {
	print CSVFILE $device;
	for $i (1..$#counterlist) {
		print CSVFILE $counterlist[$i].",";  
	}
}
print CSVFILE "\n"; 

# the follow each line
$linecount = ($#devicelist + 1) * $#counterlist;
$numsamples = ($#rawdatalist + 1) / $linecount;
for ($i = 0; $i < $numsamples; $i++) {
	for ($j = 0; $j < $linecount; $j++) {
		print CSVFILE shift(@rawdatalist);
		print CSVFILE ",";
	}
	print CSVFILE "\n";
}

close(CSVFILE);
