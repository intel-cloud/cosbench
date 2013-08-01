#for ((i=1;i<=5;i++))
for sn in `cat sn.lst`
do
ssh $sn "cd /etc/swift; ./sn-start-service.sh" 
done
