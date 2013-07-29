#!/bin/bash
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
address=127.0.0.1:19088

usage()
{
        echo
        echo "Usage: $0 <username:password> <action: submit|cancel|info> <parameter> <web ip:port>"
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

user=$1
action=$2

case $action in
        "submit")
                if [ $# -ge 4 ]; then
                        address=$4
                fi
                curl -u $user -F config=@$3 http://${address}/controller/cli/submit.action
                ;;
        "info")
                if [ $# -ge 3 ]; then
                        address=$3
                fi
                curl -u $user http://${address}/controller/cli/index.action
                ;;
        "cancel")
                if [ $# -ge 4 ]; then
                        adddress=$4
                fi
                curl -u $user -d id=$3 http://${address}/controller/cli/cancel.action
                ;;
        *)
                echo "!!!Unknown action: $action"
                usage
esac

echo
