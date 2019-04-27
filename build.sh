#!/bin/bash

echo "--------------------------Building Server--------------------"
pushd server
pushd src/main/resources/public
npm install
npm run-script build
popd
mvn clean compile assembly:single
docker build -t multisweeper/server .
popd
echo "-------------------------------------------------------------"
