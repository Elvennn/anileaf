#!/bin/sh

$1/docker stop anileaf
$1/docker build -t anileaf . && $1/docker run -v /share/Public/Animes:/Animes anileaf