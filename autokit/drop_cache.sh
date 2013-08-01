
for ((i=1;i<=10;i++))
do
ssh c$i " echo 1 > /proc/sys/vm/drop_caches"
done

for ((i=1;i<=5;i++))
do
ssh sn$i " echo 1 > /proc/sys/vm/drop_caches"
done

ssh proxy "echo 1 > /proc/sys/vm/drop_caches"
