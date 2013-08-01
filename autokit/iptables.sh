
service iptables stop
ssh proxy "service iptables stop"
for ((i=1;i<=5;i++))
do
ssh sn$i "service iptables stop; setenforce 0";
done
