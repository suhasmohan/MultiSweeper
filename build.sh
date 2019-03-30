#!/bin/bash

echo "--------------------------Building Server--------------------"
pushd server
mvn clean compile assembly:single
popd
echo "-------------------------------------------------------------"
