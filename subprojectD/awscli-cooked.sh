#!/usr/bin/env bash

# subprojectD/awscfn.sh

Template="file://./src/cloudformation/D-template.yml"
StackName="StackD"
Parameters="file://./build/parameters.json"

source ../awscli-cooked.sh


