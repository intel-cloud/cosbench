#!/bin/sh


ssh proxy "swift -A http://10.1.2.100:8080/auth/v1.0 -U sotc:admin -K intel delete --all"

for sn in sn1 sn2 sn3 sn4 sn5
do
ssh $sn "sync"
done

sleep 60
