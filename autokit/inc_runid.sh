#!/bin/bash

#-------------------------------------------
# Configurable Options
#-------------------------------------------

source ./header.sh

#-------------------------------------------
# Main
#-------------------------------------------

# Update the run number for the next test.
RUNID=`expr $RUNID + 1`

echo $RUNID > .run_number
