#!/bin/sh

protoc --java_out=java yamcs.proto
protoc --java_out=java mdb.proto
protoc --java_out=java pvalue.proto
protoc --java_out=java alarms.proto
protoc --java_out=java commanding.proto
protoc --java_out=java yamcsManagement.proto
protoc --java_out=java archive.proto
protoc --java_out=java rest.proto
protoc --java_out=java web.proto
protoc --java_out=java table.proto
