Neo GOF
=====

Author: kazurayam, japan

Date: 7th April 2020

Table of contents:
- [Overview](#overview)
- [Problem to solve](#problem)
- [Solution](#solution)
- [Prerequisites](#prerequisites)
- [Project structure](#project_structure)
- [If you want to try yourself](#if_you_want)
  - [S3 Bucket names need to be globally unique](#unique_bucket_name)
  - [One-liner to generate your identity](#identity)
  - [*.sh files must be executable](#sh_mode)
- [Description1: Neo GOF toolset](#NeoGOF)
  - [Head-first demonstration](#demo)
  - [Internal of the :subprojectD:createStack task](#createStack)
- [Description2: Gradle AWS Plugin](#gradle_aws_plugin) 
  - [subprojectA: create a S3 Bucket using dedicated Task](#subprojectA)
  - [subprojectB: create a S3 Bucket using CloudFormation via plugin](#subprojectB)
  - [subprojectC: failed to create a IAM Role using CloudFormation via plugin](#subprojectC)
    - [Root cause of failure](#InsufficientCapabilitiesException)
- [Conclusion](#conclusion)

<a name="overview" id="overview"></a>
## Overview

Build and Delivery by the toolset of
**Gradle + Shell + AWS CLI + CloudFormation** (new Gang of Four)
makes life easy for Java/Groovy/Kotlin developers.

![Neo GOF Overview](./docs/images/overview.png)

<a name="proble" id="problem"></a>
## Problem to solve

1. I want to use **[Gradle](https://gradle.org/) Build Tool** to achieve
[Continuous Delivery](https://martinfowler.com/bliki/ContinuousDelivery.html).
By one command `$ gradle deploy`, I want to achieve all tasks for developing applications 
in Java/Kotlin language which should run as 
[AWS Lambda](https://aws.amazon.com/lambda/) Funtions.
2. I want to use Gradle to do compiling, testing and archiving for my Java applications as usual.
3. I want to automate provisioning a [AWS S3](https://aws.amazon.com/s3/) Bucket where I locate the jar file containing
my Lambda functions.
4. I want to automate uploading the jar file up to the designated S3 Bucket
every after I modify the application source and rebuild it.
5. I want to automate provisioning other various AWS resources: 
AWS Lambda Functions, CloudWatch Event Rules, 
[IAM Roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles.html), Simple Queue Service, 
 Simple Notification Service, etc. In order to accomplish these complex tasks,
 I want to use 
**AWS Provisioning Tool [Cloud Formation](https://aws.amazon.com/jp/cloudformation/)**.

A question came up to me. **How can a Gradle `build.gradle` script make full use of AWS CloudFormation?** 

<a name="solution" id="solution"></a>
## Solution

I found 2 possible approaches.

1. use combination of Gradle + Shell + AWS CLI + CloudFormation:  
The `build.gradle` scripts calls built-in `Exec` task which executes 
an external shell script file [`awscli-cooked.sh`](./awscli-cooked.sh) which executes
[AWS CLI](https://aws.amazon.com/cli/) to drive CloudFormation.
2. use a Gradle AWS plugin [jp.classmethod.aws](https://github.com/classmethod/gradle-aws-plugin):
the plugin adds some Gradle tasks for managing various AWS resources including CloudFormation.

I did a research for a few days and got a conclusion that the Gang of Four toolset 
is better than a single Gradle plugin.

I will explain how the Neo GOF toolset works first.
Later I will also explain what I found about the plugin.

<a name="prerequisites" id="prerequisites"></a>
## Prerequisites

- Java 8 or higher
- Bash shell. On Windows I installed 
[Git for Windows](https://gitforwindows.org/) and got bundled "Git Bash".
- AWS Account for my use
- IAM User with enough privileges.
- [AWS CLI](https://aws.amazon.com/cli/) installed and 
[configured](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html) on my Mac/PC.
The `default` profile in `~/.aws/credentials` file is configured to point my priviledged IAM User.
- I used Mac, though this project should work on Windows and Linux as well.

<a name="project_structure" id="project_structure"></a>
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
Hello, app
Hello, subprojectA
Hello, subprojectB
Hello, subprojectB
Hello, subprojectD
Hello, rootProject
```

Or, you can execute a specific task of a subproject by typing 
`:<subPorjectName>:<taskName>`.
For example;

```
$ cd $NeoGOF
$ ./gradlew -q :subprojectA:hello
Hello, subprojectA
```

<a name="if_you_want" id="if_you_want"></a>
## If you want to try yourself

<a name="unique_bucket_name" id="unique_bucket_name"></a>
### S3 Bucket names need to be globally unique

You can download an zip archive of the project from the
[Releases](https://github.com/kazurayam/NeoGOF/releases) page.
Provided that you have seasoned experience of using Gradle
you should be able to play on this project.

If you are going to try running this project on your PC, there is one thing you need to edit.

In `[gradle.properties](./gradle.properties) file, you will find such line:

```
S3BucketNameA=bb4b24b08c-20200406-neogof-a
```

This line specifies the name of a S3 Bucket to be provisioned.
As you know, a S3 Bucket name must be globally unique. 
The Bucket name starting with `bb4b24b08c` is mine, not for your use.
So you need to edit the `gradle.properties` file so that you give 
alternative names for your own use.
I would recommend you to replace the leading `bb4b24b08c` part 
with some other string.

<a name="identity" id="identity"></a>
### One-liner to generate your identity

You want to generate your identity to make your S3 Bucket names globally unique.
Ok, you can generate a mystified (possibly globally unique) 
10 characters based on your AWS Account ID (12 digits) 
by the following shell command. 
Here I assume that you have AWS CLI installed:

```
$ aws sts get-caller-identity --query Account | md5sum | cut -c 1-10
```

<a name="sh_mode" id="sh_mode"></a>
### *.sh files need to be executable

Another thing you need to be aware. Once cloned out, it is likely that
you need to change mode of *.sh files to make them executable. I mean,
you may want to do:

```
$ cd $NeoGOF
$ chmod +x ./awscli-cooked.sh
$ chmod +x ./subprojectD/awscli-cooked.sh
```

<a name="NeoGOF" id="NeoGOF"></a>
## Description1: Neo GOF toolset

You can locate the project anywhere on your PC. 
For example I have cloned out this project to a local directory 
`/Users/myname/github/NeoGOF`.
In the following description, I will use a symbol *`$NeoGOF`* 
for short of this local path.

<a name="demo" id="demo"></a>
### Head-first demonstration

In Bash commandline, type
```
$ cd $NeoGOF
$ ./gradlew -q deploy
```

Then the following output will follow if successful:

```
neogof-0.1.0.jar has been built
created /Users/myname/github/NeoGOF/subprojectD/build/parameters.json
{
    "StackId": "arn:aws:cloudformation:ap-northeast-1:84**********:stack/StackD/99bd96c0-78c9-11ea-b8e1-060319ee749a"
}
Neo GOF project has been deployed

```

Here I wrote `84**********`. This portion is the 12 digits of my AWS Account ID.
You will see different 12 digits of your AWS Account ID when you tried yourself.

Executing `./gradlw deploy` is designed to provision 2 AWS resources.
1. a S3 Bucket named `bb4b24b08c-20200406-neogof-d`
2. a IAM Role named `NeoGofRoleD`

---

Many things will be performed behind the sean. Let me follow the code
and explain the detail.

When you type `gradlew deploy`, the `deploy` task defined in the 
[NeoGOF/build.gradle](./build.gradle) is executed.

```
task deploy(dependsOn: [
        ":app:build",
        ":subprojectD:createStack"
]) {
    doLast {
        println "Neo GOF project has been deployed"
    }
}
```

The `deploy` task calls 2 tasks: `:app:build` and `:projectD:createStack`;
and when they finished the `deploy` task shows a farewell message. Of course 
you can execute these tasks independently as:

```
$ cd $NeoGOF
$ ./gradlew :app:build
...
$ ./gradlew :subprojectD/createStack
...
```

The `app` sub-project is a small Gradle project with `java` plugin applied.

The `app` sub-project contains a Java class 
[`example.Hello`](./app/src/main/java/example/Hello.java). 
The class implements 
`com.amazonaws.services.lambda.runtime.RequestHandler`. 
Therefore the `example.Hello` class can run as a AWS Lambda Function.

The `build` task of the `app` project compiles the Java source and 
build a jar file. 
The `app` project is a typical Gradle project and has nothing new.

----

The `subprojectD` sub-project indirectly activates AWS CloudFormation to
 provision S3 Bucket and IAM role. 
 
Please note, **the `deploy` task combines a Gradle built-in feature 
(building Java application) and a extended feature 
(driving AWS CloudFormation) just seamlessly**.

<a name="createStack" id="createStack"></a>
### Internal of the `:subprojectD:createStack` task
 
The `createStack` task in 
 [`subprojectD/build.gradle`](./subprojectD/build.gradle) 
 file is like this:
 
```
task createStack {
    doFirst {
        copy {
            from "$projectDir/src/cloudformation"
            into "$buildDir"
            include 'parameters.json.template'

            rename { file -> 'parameters.json' }
            expand (
                    bucketName: "${S3BucketNameD}"
            )
        }
        println "created ${buildDir}/parameters.json"
    }
    doLast {
        exec {
            workingDir "${projectDir}"
            commandLine './awscli-cooked.sh', 'createStack'
        }
    }
}
```

The `createStack` task does 2 things.

First it executes a `copy` task.
The `copy` task prepares a set of parameters to be passed to CloudFormation Template.
It copies a template file into `build/parameters.json` while interpolating
a `$bucketName` symbol in the template to the value specified in the `rootProject/gradle.properties` file.

Let me show you an example how `parameter.json` file is prepared.

Template: `$projectDir/src/cloudformation/parameters.json.template`:

```
[
  {
    "ParameterKey": "S3BucketName",
    "ParameterValue": "${bucketName}"
  }
]
```

Values defined: `$projectDir/gradle.properties`

```
...
S3BucketNameD=bb4b24b08c-20200406-neogof-d
...
```

Output: `$buildDir/parameters.json`

```
[
  {
    "ParameterKey": "S3BucketName",
    "ParameterValue": "bb4b24b08c-20200406-neogof-d"
  }
]
``` 

The `sub_createStack` function in `awscli-cooked.sh` file will pass 
the generated `$buildDir/parameters.json` to CloudFormation.

Thus you can transfer the parameter values defined in Gradle world 
into the CloudFormation Template world.

---

Secondly the `createStack` task in `subprojectD/build.gradle` executes 
a `exec` task which executes an external bash script file 
[`awscli-cooked.sh`](./awscli-cooked.sh) with sub-command `createStack`. 
Let's have a quick look at the code fragment:

```
sub_createStack() {
    aws cloudformation create-stack --template-body $Template --parameters $Parameters --stack-name $StackName --capabilities CAPABILITY_NAMED_IAM
}
```

Any AWS developer will easily see what this shell function does.
The shell function 
`sub_createStack` invokes AWS CLI to activate CloudFormation 
for creating a Stack with several options/parameters specified as appropriate.

The shell script [`awscli-cooked.sh`](./awscli-cooked.sh) implements a few 
other subcommands: `deleteStack`, `describeStacks`, `validateTemplate`. 
All of them are one-liners which invoke AWS CLI to activate CloudFormation.

Simple and easy to understand, isn't it?

<a name="gradle_aws_plugin" id="gradle_aws_plugin"></a>
## Description2: Gradle AWS Plugin

Visit [Gradle Plugins Repository](https://plugins.gradle.org/) and 
make query with keyword `aws`. You will find quite a few Gradle plugins 
that enables managing AWS resources. I picked up 
[jp.classmethod.aws](https://plugins.gradle.org/plugin/jp.classmethod.aws).
I will show you what I tried with this plugin.

<a name="subprojectA" id="subprojectA"></a>
### subprojectA: create a S3 Bucket using dedicated Task

In the commandline with bash, I can try this:

```
$ cd $NeoGOF
$ ./gradlew :subprojectA:createBucket
```

Then I got a new S3 Bucket is successfully created in my AWS Account.

In the [subprojectA/build.gradle](./subprojectA/build.gradle) file,
I have the following task definition:

```
task createBucket(type: CreateBucketTask) {
    bucketName "${S3BucketNameA}"
    region "${Region}"
    ifNotExists true
}
```

The `CreateBucketTask` is provided by the Gradle plugin 
[jp.classmethod.aws](https://plugins.gradle.org/plugin/jp.classmethod.aws).

<a name="subprojectB" id="subprojectB"></a>
### subprojectB: create a S3 Bucket using CloudFormation via plugin

In the commandline with bash, I can try this:

```
$ cd $NeoGOF
$ ./gradlew :subprojectB:awsCfnMigrateStack
```

Then I got a new S3 Bucket is successfully created in my AWS Account.

In the [subprojectA/build.gradle](./subprojectA/build.gradle) file,
I have the following task defintion:

```
cloudFormation {
    stackName 'StackB'
    stackParams([
            S3BucketName: "${S3BucketNameB}"
    ])
    capabilityIam true
    templateFile project.file("src/cloudformation/B-template.yml")
}
// awsCfnMigrateStack task is provided by the gradle-aws-plugin
// awsCfnDeleteStack  task is provided by the gradle-aws-plugin
```

The `awsCfnMigrateStack` task is a dedicated task provided by
the Gradle plugin 
[jp.classmethod.aws](https://plugins.gradle.org/plugin/jp.classmethod.aws)
to activate AWS CloudFormation.

The [`src/cloudformation/B-template.yml`](./subprojectB/src/cloudformation/B-template.yml)
is the Template for CloudFormation Stack. It contains such code fragment:

```
Resources:
  S3Bucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub ${S3BucketName}
      AccessControl: Private
      PublicAccessBlockConfiguration:
        BlockPublicAcls: True
        BlockPublicPolicy: True
        IgnorePublicAcls: True
        RestrictPublicBuckets: True
```

This is a typical CloudFormation code that creates a S3 Bucket.

<a name="subprojectC" id="subprojectC"></a>
### subprojectC: failed to create a IAM Role using CloudFormation via plugin

In the commandline with bash, I can try this:

```
$ cd $NeoGOF
$ ./gradlew :subprojectC:awsCfnMigrateStack
```

When I tried it, it failed.

```
stack subprojectC not found

> Task :subprojectC:awsCfnMigrateStack FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':subprojectC:awsCfnMigrateStack'.
> Requires capabilities : [CAPABILITY_NAMED_IAM] (Service: AmazonCloudFormation; Status Code: 400; Error Code: InsufficientCapabilitiesException; Request ID: c1abb0f1-29c9-4679-9ca1-ccb862ff83f0)
```

The `subprojectC/build.gradle` script contains a similar code fragment 
as `subprojectBA/build.gradle`. 
```
cloudformation {
    ...
}
```
But it reads another CloudFormation Template YAML 
[`subprojectC/src/cloudformation/C-template.yml`](./subprojectC/src/cloudformation/C-template.yml).

The `C-template.yml` file contains new portion:

```
Resources:

  NeoGofRole:
    Type: AWS::IAM::Role
    Properties:
      AssumeRolePolicyDocument: ./src/iam/assume-role-policy-document.json
      RoleName: NeoGofRoleC
```

This portion requires CloudFormation to provision a IAM Role named `NameGofRoleC`.

<a name="InsufficientCapabilitiesException" id="InsufficientCapabilitiesException"></a>
#### Root cause of failure

Why `$ ./gradlew :subprojectC:awsCfnMigrateStack` failed with message 
`Error Code: InsufficientCapabilitiesException`?

The root cause is already known by the plugin developers. See the following
issue in the Project's Issue list.

- [Support for CAPABILITY_NAMED_IAM](https://github.com/classmethod/gradle-aws-plugin/issues/50)

You can see this issue was opened 4 years ago, July 2016,
and is still outstanding in April 2020.

The plugin was initially developed in 2016. Later in 2017 
`CAPABILITY_NAMED_IAM` was added in AWS CloudFormation spec.
Obvisously, the plugin has not been maintained and is now outdated. 

The originator of 
[jp.classmethod.aws](https://plugins.gradle.org/plugin/jp.classmethod.aws),
miyamoto-daisuke who has already 
[passed away](https://github.com/classmethod/gradle-aws-plugin/issues/188) unfortunately,
 commented in a open issue
[RDS Instance Support](https://github.com/classmethod/gradle-aws-plugin/issues/2)


>It is hard for me alone to implement all AWS product's feature. So I start to implement the features which I need now. I think that this plugin should have all of useful feature to call AWS API.
 Everyone can contribute to add useful features to this plugin. I appreciate your pull-requests.


So, the plugin failed to keep in pace with the rapid and continuous
development of AWS services. 


<a name="conclusion" id="conclusion"></a>
## Conclusion

I want to express my appreciations and respects to the developers of
the Gradle AWS Plugin 
[jp.classmethod.aws](https://github.com/classmethod/gradle-aws-plugin).
However the plugin is already outdated and probably will not be
maintained any longer as [the maintainer passed away](https://github.com/classmethod/gradle-aws-plugin/issues/188).

On the other hand, AWS CLI and AWS CloudFormation --- these are the AWS 
primary products. 
Therefore I can assure you that a Gradle `build.gradle` can execute 
CloudFormation via Shell+CLI in long term to go. You can
invoke everything needed to deploy your Lambda function in Java 
into your production environment by one stroke of command.
The combination of Gradle + Shell + AWS CLI + CloudFormation (Neo GOF) 
is a powerful toolset to achieve Continuous Delivery.

The Shell scripts and `build.gradle` scripts I presented here --- 
all of them are simple. Customizing them will be a breeze.
 