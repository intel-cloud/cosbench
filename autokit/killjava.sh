killall java
for ((i=1;i<=13;i++)) 
do
ssh c$i "killall java"
done
