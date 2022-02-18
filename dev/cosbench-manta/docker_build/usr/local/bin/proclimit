#!/usr/bin/env bash

# Just return the value from nproc if we are not on a Triton container
if [ ! -d /native ]; then
    nproc
    exit 0
fi

##
# Returns a number representing a very conservative estimate of the maximum
# number of processes or threads that you want to run within the zone that
# invoked this script. Typically, you would take this value and define a
# multiplier that works well for your application.

if [[ -d /native ]]; then
    PATH=$PATH:/native/usr/bin
fi

set -o errexit
if [[ -n ${TRACE} ]]; then
    set -o xtrace
fi

# CN parameters
CORES=$(kstat -C -m cpu_info -c misc -s core_id | wc -l | tr -d ' ')
PHYS_MEM=$(kstat -C -m unix -n system_pages -c pages -s physmem | cut -d':' -f5)
PAGESIZE=$(pagesize)
TOTAL_MEMORY=$(echo "${PHYS_MEM} ${PAGESIZE} * pq" | dc)

# zone parameters
ZONE_MEMORY=$(kstat -C -m memory_cap -c zone_memory_cap -s physcap | cut -d':' -f5)

# our fraction of the total memory on the CN
MEMORY_SHARE=$(echo "8k$ZONE_MEMORY $TOTAL_MEMORY / pq" | dc)

# best guess as to how many CPUs we should pretend like we have for tuning
CPU_GUESS=$(echo "${CORES} ${MEMORY_SHARE} * pq" | dc)

# round that up to a positive integer
echo ${CPU_GUESS} | awk 'function ceil(valor) { return (valor == int(valor) && value != 0) ? valor : int(valor)+1 } { printf "%d", ceil($1) }'

exit 0
