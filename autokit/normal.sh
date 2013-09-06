#!/bin/bash
#date
#

cecho()
{
echo -ne "\\033[1;31;43m" [$HOSTNAME][`/bin/date +%Y-%m-%d_%H-%M-%S`][$0] $1
echo -e "\\033[0m"
}

num_of_runs=1

#for ((i=1;i<=3;i++))
#do
cecho "======start ${i}th run"
echo ""

cecho "-----start 128K write test"
bash prepare.sh
cecho "============sleep 30s================"
cecho "start small objects read"
for ((i=1;i<=$num_of_runs;i++))
do
bash run.sh small-objects-write write_100con_100obj_128KB_
done


