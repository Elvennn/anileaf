#!/bin/sh
PATH=$1
git checkout develop
git fetch
git reset --hard origin/develop