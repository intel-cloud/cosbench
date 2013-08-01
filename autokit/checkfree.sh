
for ((i=1;i<=10;i++)) 
do
echo "free mem on c$i"
ssh c$i " free -m"
done


for ((i=1;i<=5;i++)) 
do
echo "free mem on sn$i"
ssh sn$i " free -m"
done
