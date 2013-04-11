#-------------------------------
# COSBENCH DRIVER STARTER
#-------------------------------

SERVICE_NAME=driver
VERSION=2.2

OSGI_BUNDLES="cosbench-log_${VERSION} cosbench-tomcat_${VERSION} cosbench-config_${VERSION} cosbench-http_${VERSION} cosbench-core_${VERSION} cosbench-core-web_${VERSION} cosbench-api_${VERSION} cosbench-mock_${VERSION} cosbench-ampli_${VERSION} cosbench-swift_${VERSION} cosbench-keystone_${VERSION} cosbench-driver_${VERSION} cosbench-driver-web_${VERSION}"

OSGI_CONSOLE_PORT=18089

sh cosbench-start.sh $SERVICE_NAME "$OSGI_BUNDLES" $OSGI_CONSOLE_PORT
