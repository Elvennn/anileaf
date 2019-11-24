#!/bin/sh
PATH=$1
docker stop anileaf
docker rm anileaf
docker build -t anileaf . && docker run -v /share/Public/Animes:/Animes -d --name anileaf anileaf