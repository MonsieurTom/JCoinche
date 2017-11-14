#!/usr/bin/env bash
protoc -I=./ --java_out=../Serveur/src/main/java ./CoincheGameCmd.proto
protoc -I=./ --java_out=../Client/src/main/java ./CoincheGameCmd.proto
