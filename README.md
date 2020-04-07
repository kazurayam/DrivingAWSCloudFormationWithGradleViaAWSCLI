Neo GOF
=====

Author: kazurayam

Date: 7th April 2020

## Summary

Build and Delivery by the toolset of
**Gradle + Shell + AWS CLI + CloudFormation** (new Gang of Four)
makes life easy for Java/Groovy/Kotlin developers.

![Neo GOF Overview](./docs/images/overview.png)

## Problem to solve

1. I want to use **[Gradle](https://gradle.org/) Build Tool** to achieve
[Continuous Delivery](https://martinfowler.com/bliki/ContinuousDelivery.html).
By one command `$ gradle deploy`, I want to achieve all tasks for developing applications 
in Java/Kotlin language which should run as 
[AWS Lambda](https://aws.amazon.com/lambda/) Funtions.
2. I want to automate provisioning a [AWS S3](https://aws.amazon.com/s3/) Bucket where I locate the jar file containing
my Lambda functions.
3. I want to automate uploading the jar file up to the designated S3 Bucket
every after I modify the application source and rebuild it.
4. I want to automate provisioning other various AWS resources: 
AWS Lambda Functions, CloudWatch Event Rules, 
[IAM Roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles.html), Simple Queue Service, 
 Simple Notification Service, etc. In order to accomplish these complex tasks,
 I want to use 
**AWS Provisioning Tool [Cloud Formation](https://aws.amazon.com/jp/cloudformation/)**.

A question came up to me. **How can a Gradle `build.gradle` script make full use of AWS CloudFormation?** 

I found 2 possible approaches.

1. use a Gradle plugin [jp.classmethod.aws](https://github.com/classmethod/gradle-aws-plugin):
the plugin adds some Gradle tasks for managing various AWS resources including CloudFormation.
2. use combination of Gradle + Shell + AWS CLI + CloudFormation:  
The `build.gradle` scripts calls built-in `Exec` task which executes 
an external shell script file [`awscli-cooked.sh`](./awscli-cooked.sh) which executes
[AWS CLI](https://aws.amazon.com/cli/) to drive CloudFormation.

I made a research for a few days and got a conclusion that the Gang of Four toolset 
is better than the single plugin.

I will explain how the Neo GOF toolset works first.
Later I will also explain what I found about the plugin.

## Approach 2: using Neo GOF toolset

### Prerequisites

- I have Java 8 or higher
- I have Bash shell. On Windows I installed 
[Git for Windows](https://gitforwindows.org/) and got bundled "Git Bash".
- I have an AWS Account for my use
- I have an IAM User with enough privileges.
- I have [AWS CLI](https://aws.amazon.com/cli/) installed and 
[configured](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html) on my Mac/PC
- I have the `default` profile in the `~/.aws/credentials` file is configured to point my priviledged IAM User.

### If you want to try yourself

If you are going to try running this project on your PC, there is one thing you need to change.

In `[gradle.properties](./gradle.properties) file, you will find such line:

```
S3BucketNameA=bb4b24b08c-20200406-neogof-a
```

This line specifies the name of a S3 Bucket to be provisioned.
S3 Bucket name must be unique globally. 
The Bucket name starting with `bb4b24b08c` is mine, not for your use.
So you need to edit the `gradle.properties` file so that you give alternative names for your use.
I would recommend you to replace the leading `bb4b24b08c` part to some other string.
 
You can generate a mystified 10 characters based on your AWS Account ID by the following shell command, 
provided that you have AWS CLI installed:

```
$ aws sts get-caller-identity --query Account | md5sum | cut -c 1-10
```

## Project structure

The NeoGOF project is a Gradle Multi-project, which comprises with 5 sub-projects.
```
$NeoGOF
├─app
├─subprojectA
├─subprojectB
├─subprojectC
└─subprojectD
```

On Commandline UI, I would `cd` to the rootProject, and execute `./gradlew` command.
For example, the `hello` task of the rootProject will call each `hello` tasks defined
each sub-projects.

```
$ cd $NeoGOF
$ ./gradlew -q hello
Hello, subprojectA
Hello, subprojectB
Hello, subprojectB
Hello, subprojectD
Hello, rootProject
```

Or, you can execute a specific task of a subproject by typing `:<subPorjectName>:<taskName>`.
For example;

```
$ cd $NeoGOF
$ ./gradlew -q :subprojectA:hello
Hello, subprojectA
```

## Approach 1: using Gradle plugin

Here I assume I have checked out this project to a local directory `/Users/myname/github/NeoGOF`.
I will write `$NeoGOF` for short of `/Users/myname/github/NeoGOF`.

### Quickstart

In Bash commandline, type
```
$ cd $NeoGOF
$ ./gradlew deploy
```

The ````




## Conclusion

Small Gradle plugin projects for managing AWS resources, such as 
[jp.classmethod.aws](https://github.com/classmethod/gradle-aws-plugin), 
may fail to keep pace with the quick and continuous development of AWS services.

On the contrary, AWS CLI and AWS CloudFormation --- these are the primary
products which AWS supports to make their service available to users.
Users can rely on these 2 products.
If a `build.gradle` execute an external shell scripts which calls AWS CLI,
then it can indirectly drive CloudFormation in its full scale. 
The combination of Gradle + Shell + AWS CLI + CloudFormation (Neo GOF) 
is powerful and easy to maintain in future.

Gradle plugins to manage AWS resources --- I think I would no longer need them.
I would prefer Neo GOF.







 