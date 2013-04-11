#-------------------------------
# COSBENCH DRIVER STOPPER
#-------------------------------

SERVICE_NAME=driver

OSGI_CONSOLE_PORT=18089

sh cosbench-stop.sh $SERVICE_NAME $OSGI_CONSOLE_PORT
