#!/bin/bash
host=admin@192.168.1.7
anileaf_path=/share/homes/admin/anileaf/
git_dir=/opt/bin
docker_dir=/share/CACHEDEV1_DATA/.qpkg/container-station/bin

scp ~/workspace/anileaf/daemon/build/libs/daemon-fat-1.0-SNAPSHOT.jar $host:$anileaf_path
ssh $host "cd $anileaf_path && ./updateProd.sh $git_dir && ./startDaemon.sh $docker_dir"