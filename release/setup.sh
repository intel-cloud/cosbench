#!/bin/bash

useradd cosbench

mkdir /opt/cosbench

__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cp -r ${__dir}/* /opt/cosbench/
chown -R cosbench:cosbench /opt/cosbench

cp "${__dir}/scripts/cosbench-controller.conf" "/etc/init/"

cp "${__dir}/scripts/cosbench-driver.conf" "/etc/init/"
