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

cecho "-----start 128K read test"
bash prepare.sh
cecho "============sleep 30s================"
sleep 30
cecho "start small objects read"
bash run_create.sh small-objects-read read_100con_100obj_128KB_
for ((i=1;i<=$num_of_runs;i++))
do
bash run.sh small-objects-read read_100con_100obj_128KB_
done

cecho "-----start 128K Write test"
bash prepare.sh
cecho "sleep 30s"
sleep 30
bash run_create.sh small-objects-write write_100con_100obj_128KB_
for ((i=1;i<=$num_of_runs;i++))
do
bash run.sh small-objects-write write_100con_100obj_128KB_
done


cecho "------start medium objects read----"
bash prepare.sh
cecho "sleep 30s"
sleep 30
bash run_create.sh medium-objects-read read_100con_100obj_10MB_
for ((i=1;i<=$num_of_runs;i++))
do
bash run.sh medium-objects-read read_100con_100obj_10MB_
done


cecho "---------start medium objects write----"
bash prepare.sh
cecho "sleep 30s"
sleep 30
bash run_create.sh medium-objects-write write_100con_100obj_10MB_
for ((i=1;i<=$num_of_runs;i++))
do
bash run.sh medium-objects-write write_100con_100obj_10MB_
done
