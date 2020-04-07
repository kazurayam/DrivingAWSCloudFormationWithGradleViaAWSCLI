Neo GOF
=====

Author: kazurayam

Date: 7th April 2020

## Summary

Build and Delivery by the toolset of
**Gradle + Shell + AWS CLI + CloudFormation** (new Gang of Four)
makes life easy for Java/Groovy/Kotlin developers.

## Problem to solve

1. I want to use **[Gradle](https://gradle.org/) Build Tool** to achieve
[Continuous Delivery](https://martinfowler.com/bliki/ContinuousDelivery.html).
In one command `$ gradle deploy`, I want to cover all tasks of developing application 
in Java/Kotlin language which should run as AWS Lambda Funtions.
2. I want to automate provisioning a AWS S3Bucket where I locate the jar file containing
my Lambda functions.
3. I want to automate uploading the jar file up to the designated S3 Bucket
everytime I modified the application source and rebuild it.
4. I want to automate provisioning other various AWS resources: 
AWS Lambda Functions, CloudWatch Event Rules, IAM Roles, Simple Queue Service, 
 Simple Notification Service, etc.
In order to accomplish these complex tasks, I want to use 
**AWS Provisioning Tool [Cloud Formation](https://aws.amazon.com/jp/cloudformation/)**.

A question came up to me. 

>How can a `build.gradle` script of Gradle make full use of AWS CloudFormation? 

I found 2 possible approaches.

1. use [Gradle Aws Plugin](https://github.com/classmethod/gradle-aws-plugin):
the plugin adds some Gradle tasks for managing various AWS resources including CloudFormation.
2. Neo GOF toolset: Gradle + Shell + AWS CLI + CloudFormation. 
The `build.gradle` scripts calls built-in `Exec` task which executes 
an external shell script file [`awscli-cooked.sh`](./awscli-cooked.sh) which executes
[AWS CLI](https://aws.amazon.com/cli/) to drive CloudFormation.

I made a research for a few days and got a conclusion that Neo GOF toolset is better than the plugin.
I will explain how NeoGOF toolset works first.
Later I will also explain what I found about the plugin.

## Demonstration of Neo GOF toolset

### Prerequisites

- I have Java 8 or higher
- I have AWS Account for my use,
- I have a IAM User for you with enough privileges.
- I have [AWS CLI](https://aws.amazon.com/cli/) is installed and configured.
- I have the `default` profile in `~/.aws/credentials` is configured to point my IAM User.


## Discussion about the alternative approach






 