#!/bin/bash

git checkout develop
git fetch
git reset --hard origin/develop
docker stop anileaf
docker build -t anileaf .
docker run -d anileaf