
#for ((i=1;i<=5;i++))
for sn in `cat sn.lst`
do
ssh $sn "ntpdate -u 192.168.3.101" &
done


ssh proxy "ntpdate -u 192.168.4.254" &
for ((i=1;i<=12;i++))
do
ssh c$i "ntpdate -u 10.1.2.110" &
done
