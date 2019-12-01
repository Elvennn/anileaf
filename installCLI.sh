#!/bin/sh

gradle fatjar && \
sudo mkdir -p /usr/share/anileaf && \
sudo cp cli/build/libs/cli-fat-*.jar /usr/share/anileaf/anileafCLI.jar && \
sudo cp anileaf /usr/bin/ && \
echo DONE