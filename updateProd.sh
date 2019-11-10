#!/bin/bash
host=admin@192.168.1.7
anileaf_path=/share/homes/admin/anileaf/

scp ~/workspace/anileaf/daemon/build/libs/daemon-fat-1.0-SNAPSHOT.jar $host:$anileaf_path
ssh $host "cd $anileaf_path && ./startDaemon.sh"
