#!/bin/bash

echo "--------------------------Building Server--------------------"
pushd server
mvn clean compile assembly:single
docker build -t multisweeper/server .
popd
echo "-------------------------------------------------------------"
