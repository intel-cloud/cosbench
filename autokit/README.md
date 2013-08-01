COSBench Automation Kit
=======================

This is one automation kit for Openstack swift performance measurement with COSBench, it leverages bash and perl scripts 
to help automate the one performance measure procedure, including setup/teardown storage cluster, kicking off load generation, 
processing performance statistics. 

So far some parameters or actions are swift specific, tweaks are expected to adapt to your setup. 


Preliminary
-----------
1. Setting up no-password ssh/scp : BKM_setting up no-password rsh/scp 
2. check if ssh, scp, sar, iostat, vmstat, perl etc tools installed
3. Modify NodeList, ProxyList, ClientList files to reflect your real settings.


Installation and Usage
----------------------
0. copy its software to the local Linux machine 
1. Modify NodeList: add the nodes (IP or hostname) which you want to monitor 
2. Modify header.sh : define configurable parameters for other shell use 
  2.1: RESULTDIR: define your own name of result directory
3. Execute install.sh : for first run to install the scripts to the nodes in NodeList 
4. Modify run.sh: scripts for a single run
	4.1: $COSBENCH for your COSBench path, $CONFIG_DIR and $CONFIG_LIST
	4.2: $REMOTE_SERV and $REMOTE_DIR for where the data will be copied to
5. summary_v1.0.xlsm: Excel spreadsheet for post-processing
6. Modify all.sh: change this scripts for multiple run


Scripts
-------
	run_sysstat.sh : to collect sar/iostat/vmstat data in the nodes in NodeList 
	stop_sysstat.sh : to stop collecting sar/iostat/vmstat data in the nodes in NodeList
	process_sysstat.sh post-processing the sar/iostat/vmstat data
	remote_copy.sh : to copy all the data from nodes in NodeList to the local machine 
	output : directory to hold results 
	verify.sh : to check whether the nodes in NodeList can be pinged, sshed, execute vmstat etc
	clean_sysstat.sh: clean the log and csv file on remote hosts


recommend calling step
----------------------
1. stop_sysstat.sh
2. clean_sysstat.sh
3. start_sysstat.sh
4. kick off workload
5. stop_sysstat.sh
6. process_sysstat.sh
7. remote_copy.sh


Troubleshooting
---------------
1. Failed to collect data in localhost
A: Also need to enable no-password ssh/scp on localhost

2. Only collect data in one node, seems like don't start work on multi nodes 
A: need to add "-f" option to SSH command


== END
