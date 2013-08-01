
echo "Check client..."
for ((i=1;i<=12;i++))
do
echo "----Check client $i----"
ssh c$i "ethtool eth0 |grep Mb/s"
done

echo "Check storage node"
for ((i=1;i<=5;i++))
do
echo "-----Check sn$i NIC speed-------"
ssh atom-sn$i "echo Eth0: ethtool eth0 |grep Mb/s; echo Eth1; ethtool eth1 |grep Mb/s; echo eth2; ethtool eth2 |grep Mb/s; echo eth3; ethtool eth3 |grep Mb/s; echo eth4; ethtool eth4 |grep Mb/s; echo eth5; ethtool eth5 |grep Mb/s; echo eth6; ethtool eth6 |grep Mb/s"
done

for ((i=1;i<=5;i++))
do
echo "-==----------Check sn$i bonding statusi------------"
ssh atom-sn$i "cat /proc/net/bonding/bond0 |grep MII"
done
