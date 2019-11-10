#!/bin/sh

$1/git checkout develop
$1/git fetch
$1/git reset --hard origin/develop