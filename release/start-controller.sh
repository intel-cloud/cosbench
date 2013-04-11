#-------------------------------
# COSBENCH CONTROLLER STARTER
#-------------------------------

SERVICE_NAME=controller
VERSION=2.2

OSGI_BUNDLES="cosbench-log_${VERSION} cosbench-tomcat_${VERSION} cosbench-config_${VERSION} cosbench-core_${VERSION} cosbench-core-web_${VERSION} cosbench-controller_${VERSION} cosbench-controller-web_${VERSION}"

OSGI_CONSOLE_PORT=19089

sh cosbench-start.sh $SERVICE_NAME "$OSGI_BUNDLES" $OSGI_CONSOLE_PORT
