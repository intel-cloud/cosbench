
echo "Restarting cosbench and proxy..."
echo "stop-csobench"
./stop-cosbench-cluster.sh
./killjava.sh 
sleep 5
echo "stop storage node"
./stop-sn.sh
sleep 5
echo "start storage node"
./sn-start.sh
sleep 5
echo "restart proxy"
./proxy.sh
sleep 5
echo "start cosbench"
./start-cosbench-cluster.sh
sleep 10
./status-cosbench-cluster.sh
