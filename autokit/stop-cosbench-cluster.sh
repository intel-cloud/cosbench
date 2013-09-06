cd /cosbench/2.0.0
sh stop-controller.sh
ssh root@c1 "cd /cosbench/2.0.0; sh stop-driver.sh" &
ssh root@c2 "cd /cosbench/2.0.0; sh stop-driver.sh" &
ssh root@c3 "cd /cosbench/2.0.0; sh stop-driver.sh" &
ssh root@c4 "cd /cosbench/2.0.0; sh stop-driver.sh" &
ssh root@c5 "cd /cosbench/2.0.0; sh stop-driver.sh" &
for ((i=6;i<=12;i++))
do
ssh root@c${i} "cd /cosbench/2.0.0; sh stop-driver.sh" &
done

echo "cosbench cluster stopped"
