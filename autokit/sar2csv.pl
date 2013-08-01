#!/usr/bin/perl

#$sarfile = "$ARGV[0]\\sar.log";
#$CSVFILE = "$ARGV[0]\\sar.csv";
$sarfile = $ARGV[0];
$CSVFILE = $ARGV[1];

# --------------------------------------------------------------------
# This reads through the entire SAR log file, builds a list of unique
# counter names (counterlist), and builds a list of data (sarlist).
# --------------------------------------------------------------------

open(SARFILE, $sarfile) || die "Unable to open $sarfile!\n";

my @ARRSARFILE = <SARFILE>;

close(SARFILE);

foreach $sline (@ARRSARFILE)
{
  ($c1, $c2, $c3, $c4, $c5, $c6) = split(" ", $sline);

  $counter = (index($c4, "-") > -1) ? $c5 : $c4." ".$c5;

  if(!exists($counterlist{$counter}))
  {
    push(@counterarr, $counter)
  }
    push(@{$counterlist{$counter}},$c6);
 
}

# --------------------------------------------------------------------
# Now the data for each counter may be extracted and stored in an
# associative array (data).
# --------------------------------------------------------------------

foreach $counter (keys %counterlist)
{
  @coldata = @{$counterlist{$counter}};

  $numsamples = 0;

  foreach $sample (@coldata)
  {
    $data{$counter, $numsamples} = $sample;
    $numsamples+=1;
  }
}


# --------------------------------------------------------------------
# This merge all data into a single csv file.
# --------------------------------------------------------------------

open(CSVFILE, ">$CSVFILE") || die "Unable to open $CSVFILE!\n";

foreach $counter (@counterarr)
{
   print CSVFILE $counter.",";
}
print CSVFILE "\r\n";

$line = 0;

while ($line < $numsamples)
{
  if ($line < $numsamples)
  {
    foreach $counter (@counterarr)
    {
      print CSVFILE $data{$counter, $line}.",";
    }
    $line ++;
  }
  print CSVFILE "\r\n";
}

close(CSVFILE);
