#!/bin/bash

bash stop-sn-proxy.sh
#bash stop_rest.sh 
#echo "sleep 30s"
#sleep 30
bash start-sn-proxy.sh
sleep 10

ssh sn1 "rm -rf /var/log/syslog; service rsyslog restart"
ssh sn4 "rm -rf /var/log/syslog; service rsyslog restart"

bash stop-cosbench-cluster.sh
bash status-cosbench-cluster.sh
bash start-cosbench-cluster.sh
