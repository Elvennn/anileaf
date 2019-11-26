# Anileaf

WARNING: I develop this tools to feat my needs, it probably cannot be used as is by someone else.

Anileaf is a tool allowing you to automate the management of your anime library
and synchronize your watching history with Anilist.co

It's strongly inspired by the great [Taiga](https://github.com/erengy/taiga), but runs on Linux.

## Features

Anileaf is separated in 3 projects :
- A Command Line Interface
- A Daemon running recurrent actions  
- A core library sharing common features 
- A C program which uses Linux's [Inotify](http://man7.org/linux/man-pages/man7/inotify.7.html)
to watch for watching episodes

### Daemon

- Download new anime episodes using a Torrent RSS
- Can send SMS notification when a new anime is downloaded 
- Update anime list when an anime is watched

### CLI

- `anileaf init` : Initialize anileaf with anilist.co credentials
- `anileaf catchup` : Search previous episodes in Torrent RSS and download missing ones
- `anileaf list` : Show current anime list 
- `anileaf next` : List all downloaded episodes not watched yet
- `anileaf play <anime>` : Play next episode for anime
- `anileaf sync` : Synchronize with anilist.co
- `anileaf update <anime> <episode>` : Update current episode for anime 

## Install

To build : `gradle fatJar`

To install the CLI use : `sudo ./installCLI.sh`

Using the daemon is a bit more complexe. You can use the `Dockerfile` as an example.