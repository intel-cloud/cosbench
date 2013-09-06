#!/bin/bash

for node in `cat sn.lst`
do
  echo $node
  ssh root@$node "cd /etc/swift; sh sn-stop-service.sh; chown -R swift:swift /etc/swift; chown -R swift:swift /srv/node/*; cd /etc/swift; sh sn-start-service.sh" &
done

for node in `cat proxy.lst`
do
  echo $node
  ssh root@$node "cd /etc/swift; sh proxy-stop-service.sh; chown -R swift:swift /etc/swift; cd /etc/swift; sh proxy-start-service.sh"
done

sleep 4

