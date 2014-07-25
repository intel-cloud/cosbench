#/bin/bash

#    This is just a sample to show how to write trigger script.
# 1. The trigger feature could be enabled by set trigger attribute in workload configuration file, 
#      it should be like: trigger="<script_name>.sh(<arg1>,<arg2>,...)"
# 2. Currently trigger attribute support <workload> and <stage> level
# 3. Script files should be put in scripts/ directory
# 4. Script writer should enable '-k' option for a script to kill all the sub process after a workload/stage finished
# 5. User could see all script output logs in archive/<workload_name>/scripts.log after a workload finished


dir=<directory_to_store_collected_data>
df_run_time=300
node_lst=<file_to_set_node_list>

if [ !-e $node_lst ]
then
	echo "!!$node_lst doesn't exists!!"
	echo "current working directory is `pwd`"
fi

if [ "$1" = "-k" ]; then
	echo "==== stop collecting data===="
	echo "All results will be saved to $dir"
	for node in `cat $node_lst`
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
for node in `cat $node_lst`
do 
	echo "starting iostat on $node..."
	ssh ${node} "iostat -dxm 1 ${run_time} > /opt/iostat.txt &"
done

