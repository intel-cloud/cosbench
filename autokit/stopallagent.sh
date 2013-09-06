cosdir=/cosbench/2.0.0

for client in `cat client.lst`
do
	echo $client
	ssh root@$client "cd ${cosdir}; sh stop-driver.sh"

done

cd ..
sh stop-all.sh
