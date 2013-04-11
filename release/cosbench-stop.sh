#-------------------------------
# COSBENCH SHUTDOWN SCRIPT
#-------------------------------

SERVICE_NAME=$1

OSGI_CONSOLE_PORT=$2

TOOL="nc"

echo "Stopping cosbench $SERVICE_NAME ... "

ps aux | grep eclipse | grep java | grep $OSGI_CONSOLE_PORT >> /dev/null

if [ $? -ne 0 ];
then
        echo "No cosbench $SERVICE_NAME seems to be running."
        exit 0
fi

which $TOOL 1>&2 >/dev/null
if [ $? -eq 0 ]; then
	echo "exit" | $TOOL localhost $OSGI_CONSOLE_PORT >> /dev/null
else
	pid=`ps -eo pid,cmd |grep java |grep $SERVICE_NAME |cut -d" " -f1`
	kill $pid
fi

sleep 3
	
ps aux | grep eclipse | grep java | grep $OSGI_CONSOLE_PORT >> /dev/null
if [ $? -eq 0 ]; then
	echo "Can't stop cosbench $SERVICE_NAME, please inspect the system to fix it!"
	exit 1
fi

echo "Successfully stopped cosbench $SERVICE_NAME."
