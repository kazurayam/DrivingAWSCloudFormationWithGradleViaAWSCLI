#!/usr/bin/env bash

# subprojectD/awscfn.sh

Template="file://./src/cloudformation/D-template.yml"
StackName="StackC"
Parameters="file://./build/parameters.json"

source ../awscfn.sh


