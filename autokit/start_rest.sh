
#for sn in sn1 sn2 sn3 sn4 sn5

for sn  in `cat sn.lst`
do
echo "start rest server on $sn"
ssh $sn "swift-init object-replicator start; swift-init object-updater start; swift-init object-auditor start; swift-init container-replicator start; swift-init container-updater start; swift-init container-auditor start; swift-init account-replicator start; swift-init account-auditor start"  &
done


sleep 15
