#!/bin/bash

#-------------------------------------------
# Configurable Options
#-------------------------------------------

SSHCMD="ssh -f -o StrictHostKeyChecking=no"
SCPCMD="scp"
INSTALLDIR=/multiperf
OUTPUTDIR=/temp/multiperf
INTERVAL=1

# a spike is the value is greater or lesser 50x of average
SPIKETHRESHOLD=50

RUNID=-1
if [ -f ".run_number" ]; then
        read RUNID < .run_number
fi
if [ $RUNID -eq -1 ]; then
        RUNID=0
fi

RESULTRT=/var/cache/multiperf
RESULTDIR=$RESULTRT/run$RUNID

export SSHCMD SCPCMD INSTALLDIR OUTPUTDIR INTERVAL RUNID RESULTRT RESULTDIR SPIKETHRESHOLD
