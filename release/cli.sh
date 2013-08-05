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
username=""
password=""

usage()
{
        echo
        echo "Usage: $0 <action: submit|cancel|info> <parameter> <web username:password@ip:port>"
        echo "  - action:"
        echo "          - <submit> <configuration file>: submit configuration and start workload"
        echo "          - <cancel> <workload id>: cancel workload"
        echo "          - <info>: check status"
        echo "  - <web username:password@ip:port> anonymous:cosbench@127.0.0.1:19088 by default"
        echo
}
check()
{
	if [[ "${info}" == ?*:?*@?*.?*.?*.?*:?* ]]; then
		address=${info#*@}
		userinfo=${info%@*}
		username=${userinfo%:*}
		password=${userinfo#*:}
	elif [[ "${info}" != *@?*:?* ]] && [[ "${info}" == ?*.?*.?*.?*:?* ]]; then
		address=${info}	
	elif [[ "${info}" != *@?*:?* ]] && [[ "${info}" != ?*:*@* ]] && [[ "${info}" == ?*:?* ]]; then
		userinfo=${info}	
		username=${userinfo%:*}
		password=${userinfo#*:}
	else
		usage
		exit 1
	fi
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
                        info=$3
			check
                fi
				curl -F config=@$2 "http://${address}/controller/cli/submit.action?username=${username}&password=${password}"
                ;;
        "info")
                if [ $# -ge 2 ]; then
                        info=$2
			check
                fi
                curl "http://${address}/controller/cli/index.action?username=${username}&password=${password}"
                ;;
        "cancel")
                if [ $# -ge 3 ]; then
                        info=$3
			check
                fi
                curl -d id=$2 "http://${address}/controller/cli/cancel.action?username=${username}&password=${password}"
                ;;
        *)
                echo "!!!Unknown action: $action"
                usage
esac

echo
