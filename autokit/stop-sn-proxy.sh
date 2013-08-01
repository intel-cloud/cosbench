#!/bin/bash

for node in `cat sn.lst`
do
  echo $node
  ssh root@$node " cd /etc/swift; sh sn-stop-service.sh; " &
done

for node in `cat proxy.lst`
do
  echo $node
  ssh root@$node "cd /etc/swift; sh proxy-stop-service.sh; "
done

sleep 4
