#!/usr/bin/perl
# calcuate average of sar, iostat data, and put it into csv file

if ( @ARGV < 2 ) {
  die ("Usage: csv2avg.pl <input> <output> [spikeThreshold]\n");
}

$csv_file_name = "$ARGV[0]";
$summary_file_name = "$ARGV[1]";

if ($ARGE[2]) { 
	$spikeThreshold = "$ARGV[2]";
}
else {
	$spikeThreshold = 50.0;
}

my(@headers);
my(@samples);
my(@numSamples);
my(@spikes);

open(CSVFILE, $csv_file_name) || die "Unable to open $csv_file_name!!!\n";
$lineNum=1;
for (;;) {
  $line = <CSVFILE>;
  if ($lineNum == 2){
    $line = <CSVFILE>;
    $lineNum++;
    #print "The first row of data is ignored.\n";
  }   
  last unless defined($line);
  $colIdx=0;
  while ($line ne ""){
    if ($line =~ /([^,]*),(.*)/) {
      $field=$1;
      $line=$2;
    }else{
      $field=$line;
      $line="";
    }
    if ($field =~/^"(.*)"$/) {
      $field = $1;
    }
    #print $field."\n";
    if ($lineNum==1) {
      $headers[$colIdx]=$field;
      $samples[$colIdx]=0;
      $numSamples[$colIdx]=0;
      #print $headers[$colIdx]."\n";
    }else{
      if ($numSamples[$colIdx] >= 3) {
      	$avg=$samples[$colIdx]/$numSamples[$colIdx];
      	
          $samples[$colIdx]+=$field;
          $numSamples[$colIdx]++;
        }
      }else{
        $samples[$colIdx]+=$field;
        $numSamples[$colIdx]++;
      }      
    }
    $colIdx++;
  } #while 
  $lineNum++;
}
close(CSVFILE);

open(SUMFILE, ">$summary_file_name") || die "Unable to open $summary_file_name!!!\n";
for ($i=0; $i<@headers; $i++) {
  #print $headers[$i]."\n";
  if ($numSamples[$i] != 0) {
    $avg = $samples[$i]/$numSamples[$i];
  }else{
    $avg = "N/A";
  }
  if ( defined($spikes[$i]) && ($spikes[$i]>0) ) {
#    $avg .= " (".$spikes[$i]." spikes found!)";
# xuekun: write in csv format 
    $avg .= ", (".$spikes[$i]." spikes found!)";
  }
#  print SUMFILE $headers[$i]."=".$avg."\n";
# xuekun: write in csv format 
  print SUMFILE $headers[$i].",".$avg."\n";
}
close(SUMFILE);
