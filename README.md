# MultiSweeper
Multiplayer Minesweeper with distributed back-end

# Build Instructions
Download and install Docker - https://www.docker.com/products/docker-desktop
```sh
$ ./build.sh
$ docker swarm init #Only run once
$ docker stack deploy -c docker-compose.yml multisweeper 
```
## Docker commands

### Run Docker Stack
```sh
$ docker stack deploy -c docker-compose.yml multisweeper
```

### Stop Docker Stack
```sh
$ docker stack rm multisweeper
```

### View logs
```sh
$ docker service logs -f <service name>
```
The service name can be found from the below command.

### Check the status of the Docker service
```sh
$ docker service ls
ID                  NAME                  MODE                REPLICAS            IMAGE                        PORTS
lmxe61yyf4di        multisweeper_server   replicated          0/3                 multisweeper/server:latest   *:8080->8080/tcp
```

### See all running Docker containers
```sh
$ docker container ls
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS               NAMES
804449766afc        multisweeper/server:latest   "java -jar /usr/loca…"   54 seconds ago      Up 51 seconds                           multisweeper_server.2.zxmhkwzwqrolz523zcwpstwxc
cff3e614f39f        multisweeper/server:latest   "java -jar /usr/loca…"   54 seconds ago      Up 52 seconds                           multisweeper_server.1.4agwv1884ssqpfxgd9ro8xg5c
93becc3bbdf7        multisweeper/server:latest   "java -jar /usr/loca…"   54 seconds ago      Up 52 seconds                           multisweeper_server.3.n5o4lbzekbcifihusxfepok8n
```

###  SSH into a running container
Get the Container ID of the container you wish to SSH into by running ```docker container ls```
```sh
$ docker exec -it <container ID> bash
root@cff3e614f39f:/# 
```
