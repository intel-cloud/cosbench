#!/bin/bash

address=127.0.0.1:19088

usage()
{
        echo
        echo "Usage: $0 <action: submit|cancel|info> <parameter> <web ip:port>"
        echo "  - action:"
        echo "          - <submit> <configuration file>: submit configuration and start workload"
        echo "          - <cancel> <workload id>: cancel workload"
        echo "          - <info>: check status"
        echo "  - <web ip:port> 127.0.0.1:19088 by default"
        echo
}

which curl 1>&2 >/dev/null
if [ $? -ne 0 ]; then
	echo "!!! The script depends on curl, please install curl firstly. !!!"
	exit 100
fi

if [ $# -lt 1 ]; then
        usage

        exit 1
fi

action=$1

case $action in
        "submit")
                if [ $# -ge 3 ]; then
                        address=$3
                fi
                curl -F config=@$2 http://${address}/controller/cli/submit.action
                ;;
        "info")
                if [ $# -ge 2 ]; then
                        address=$2
                fi
                curl http://${address}/controller/cli/index.action
                ;;
        "cancel")
                if [ $# -ge 3 ]; then
                        adddress=$3
                fi
                curl -d id=$2 http://${address}/controller/cli/cancel.action
                ;;
        *)
                echo "!!!Unknown action: $action"
                usage
esac

echo
