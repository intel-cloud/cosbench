#!/bin/bash

results=$1
rm -rf ./$results
mkdir ./$results
mkdir ./$results/raw

cp -r /opt/graphite/storage/whisper/stats ./$results/raw/


echo "Getting proxy detail data from proxy-logging"
mkdir ./$results/proxy
/whisper-0.9.10/bin/whisper-dump.py /opt/graphite/storage/whisper/stats/timers/proxy/proxy-server/object/PUT/201/timing/mean.wsp > ./$results/proxy/proxy_PUT.csv


echo "Step1: getting proxy-server detail data"
mkdir ./$results/proxy-server
if [ -d /opt/graphite/storage/whisper/stats/timers/proxy-server ];
then
	list=`ls /opt/graphite/storage/whisper/stats/timers/proxy-server/object/PUT`
	for file in ${list[*]}
	do
	/whisper-0.9.10/bin/whisper-dump.py /opt/graphite/storage/whisper/stats/timers/proxy-server/object/PUT/$file/timing/mean.wsp > ./$results/proxy-server/$file.csv
	done
fi

echo "Step2: Get storage node data"
for node in `cat sn.lst`
do
	echo "Getting $node data"
	mkdir ./$results/$node
	list=(`ls /opt/graphite/storage/whisper/stats/timers/$node/object-server |grep -v dev`)
	for file in ${list[*]}
	do
	/whisper-0.9.10/bin/whisper-dump.py /opt/graphite/storage/whisper/stats/timers/$node/object-server/$file/timing/mean.wsp > ./$results/$node/$file.csv
	done
done
echo "Step3: get per device data"
for node in `cat sn.lst`
do
list=(` ls /opt/graphite/storage/whisper/stats/timers/$node/object-server/PUT/ |grep -v timing`)
	for dev in ${list[*]}
	do
	/whisper-0.9.10/bin/whisper-dump.py /opt/graphite/storage/whisper/stats/timers/$node/object-server/PUT/$dev/mean.wsp > ./$results/$node/PUT-${dev}.csv
	done

done

echo "Step4: get per device breakdown data"
for node in `cat sn.lst`
do
	for dev in `cat dev.lst`
	do
	mkdir ./$results/$node/$dev
	list=(` ls /opt/graphite/storage/whisper/stats/timers/$node/object-server/$dev/`)
		for dir in ${list[*]}
		do
		/whisper-0.9.10/bin/whisper-dump.py /opt/graphite/storage/whisper/stats/timers/$node/object-server/$dev/$dir/mean.wsp > ./$results/$node/${dev}/${dir}.csv
		done
	done
done
	
	


scp -r ./$results root@192.168.3.240:/mnt/sdc1/statsd/
