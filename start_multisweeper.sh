#!/bin/bash
if [ "$1" = "build" ]; then
    ./build.sh
fi

java -jar server/target/multisw*.jar $PWD/board.txt

docker stack deploy -c docker-compose.yml multisweeper
