#!/bin/sh
PATH=$1:$PATH
mkdir -p animewatch/bin
gcc animewatch/src/animewatch.c -o animewatch/bin/animewatch