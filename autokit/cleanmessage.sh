
for sn in sn1 sn2 sn3 sn4 sn5
do
ssh $sn "cd /var/log; rm -rf message*; service rsyslog restart";
done
