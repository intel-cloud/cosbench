#!/bin/bash


ssh proxy "swift -A http://10.1.2.100:8080/auth/v1.0 -U sotc:admin -K intel $1 $2 $3"


