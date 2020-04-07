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


## Demonstration of Neo GOF

## Description






 