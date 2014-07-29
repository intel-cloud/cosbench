#/bin/bash
#
#Copyright 2013 Intel Corporation, All Rights Reserved.
#
#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.
#

#-------------------------------
# COSBENCH TRIGGER SCRIPT SAMPLE
#-------------------------------

# 1. The trigger feature could be enabled by set trigger attribute in workload configuration file, 
#      it should be like: trigger="<script_name>.sh(<arg1>,<arg2>,...)"
# 2. Currently trigger attribute support <workload> and <stage> level
# 3. Script files should be put in 'scripts/' directory
# 4. Script writer should enable '-k' option for a script to kill all the sub process after a workload/stage finished
# 5. User could see all script output logs in archive/<workload_name>/scripts.log after a workload finished


#directory to store collected data
dir=/results

#default run time of iostat
df_run_time=300

#nodes array 
nodes=(ceph01 ceph02 ceph03)


if [ $cnt -le 0 ]
then
	echo "!!nodes doesn't exists!!"
fi

if [ "$1" = "-k" ]; then
	echo "==== stop collecting data===="
	echo "All results will be saved to $dir"
	for node in ${nodes[*]}
	do
		echo "stopping iostat on $node..."
		ssh ${node} "killall -9 iostat"
		tmp_dir=$dir/$node
		if [ -d $tmp_dir ]
		then
			echo "$tmp_dir already exist, Check it first !"
			exit 1
		fi
		mkdir -p $tmp_dir
		echo "copying iostat data from $node..."
		scp ${node}:/opt/iostat.txt $tmp_dir
		ssh ${node} "rm -f /opt/iostat.txt"
	done
	echo "iostat have stoped."
	exit
fi

if [ $# != 1 ] ; then
	echo " "
	echo "Description:"
    echo "    This script attempts ."
    echo " "
    echo "usage:"
    echo "    $0 run_time"
    echo " "
    exit
fi

run_time=$1
if [ ! $run_time ]; then run_time=$df_run_time ; fi

echo "==== start collecting data ===="
for node in ${nodes[*]}
do 
	echo "starting iostat on $node..."
	ssh ${node} "iostat -dxm 1 ${run_time} > /opt/iostat.txt &"
done

