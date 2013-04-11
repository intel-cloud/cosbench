#-------------------------------
# COSBENCH CONTROLLER STOPPER
#-------------------------------

SERVICE_NAME=controller

OSGI_CONSOLE_PORT=19089

sh cosbench-stop.sh $SERVICE_NAME $OSGI_CONSOLE_PORT
