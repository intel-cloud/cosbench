echo "CONTROL NODE"
ps aux | grep eclipse | grep java
for node in c1 c2 c3 c4 c5 c6 c7 c8 c9 c10 c11 c12
do  
    echo "CLIENT NODE: $node"  
    ssh root@$node "ps aux | grep eclipse" | grep java
done
