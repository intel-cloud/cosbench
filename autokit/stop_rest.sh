
#for sn in sn1 sn2 sn3 sn4 sn5
for sn in `cat sn.lst`
do
echo "stop rest server on $sn"
ssh $sn "swift-init object-replicator stop; swift-init object-updater stop; swift-init object-auditor stop; swift-init container-replicator stop; swift-init container-updater stop; swift-init container-auditor stop; swift-init account-replicator stop; swift-init account-auditor stop" &
done


sleep 15
