#!/bin/sh

#
# NeoGOF/awscnf.sh --- a shell script that calls AWS CLI to create/delete/describe AWS CloudFormation Stack
#
# You need to specify values of the shell variable "Templte" and "StackName"

ProgName=$(basename $0)

sub_help(){
    echo ""
    echo "Usage: $ProgName <subcommand> [options]\n"
    echo "Subcommands:"
    echo "    createStack"
    echo "    describeStacks"
    echo "    deleteStack"
    echo "    validateTemplate"
    echo ""
    echo "For help with each subcommand run:"
    echo "$ProgName <subcommand> -h|--help"
    echo ""
    echo "CFN Template  : $Template"
    echo "CFN Stack name: $StackName"
    echo ""

}

sub_createStack() {
    aws cloudformation create-stack --template-body $Template --parameters $Parameters --stack-name $StackName --capabilities CAPABILITY_NAMED_IAM
}

sub_describeStacks() {
    aws cloudformation describe-stacks --stack-name $StackName
}

sub_deleteStack(){
    aws cloudformation delete-stack --stack-name $StackName
}

sub_validateTemplate(){
    aws cloudformation validate-template --template-body $Template
}

subcommand=$1
case $subcommand in
    "" | "-h" | "--help")
        sub_help
        ;;
    *)
        shift
        sub_${subcommand} $@
        if [ $? = 127 ]; then
            echo "Error: '$subcommand' is not a known subcommand." >&2
            echo "       Run '$ProgName --help' for a list of known subcommands." >&2
            exit 1
        fi
        ;;
esac
