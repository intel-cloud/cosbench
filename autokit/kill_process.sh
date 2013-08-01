#!/bin/sh
if [ $# -gt 0 ]; then
process_name=$1
ps_count=`ps -ef | grep -v 'grep' | grep -v 'kill' | grep -i -c $process_name`
array_name=`ps -ef | grep -i $process_name | grep -v 'grep' | grep -v 'kill' | awk '{print $2}' | sort -u`
#echo "ps -ef | grep -i $process_name | grep -v 'grep' | grep -v 'kill' | awk '{print \$2 \$8}' | sort -u -n"

echo "${array_name[*]}"
if [ $ps_count -ne 0 ]; 
then
echo $1
kill -9 ${array_name[0]} >/dev/null 
fi
echo

else
echo "Usage: $0 process_name"
echo ""
exit
fi 
