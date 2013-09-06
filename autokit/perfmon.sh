
ssh proxy "iostat -dx 1 300 > iostat.dat&; mpstat -P ALL 1 300> mpstat.dat&"
for ((i=1;i<=5;i++))
do

ssh sn$i "iostat -dx 1 300 > iostat.dat&; mpstat -P ALL 1 300 > mpstat.dat&"
done
ssh c1 "iostat -dx 1 300 > iostat.dat&;  mpstat -P ALL 1 300 > mpstat.dat&"
ssh c5 "iostat -dx 1 300 > iostat.dat&; mpstat -P ALL 1 300 >  mpstat.dat&"
ssh c9 "iostat -dx 1 300 > iostat.dat&; mpstat -P ALL 1 300 > mpstat.dat&"
