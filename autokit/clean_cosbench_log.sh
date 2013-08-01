
for ((i=1;i<=12;i++))
do
echo "clean c$i"
ssh c$i "rm -rf /cosbench/2.0.0/log/mission/*" 
ssh c$i "df -h"
done
