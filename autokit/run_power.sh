
rm -rf /power/SPECptd/power.csv
cd /power/SPECptd;
./runpower.sh >  /dev/null 2>&1 &

sleep 90

perl /power/startp.pl 192.168.0.90 8888 1000 300 > /dev/null 2>&1 &
sleep 200
perl /power/stopp.pl 192.168.0.90 > /dev/null 2>&1 &
killall ptd-linux-x86


