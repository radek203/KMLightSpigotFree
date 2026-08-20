KMLightSpigot
===========

Warning: I do not take responsibility for the usage of this project in a production environment! It is only the public version of this project. You bear full responsibility for securing and configuring your lobby server, proxy server, etc.

## What is this project?

This server is built to handle thousands of players (For safety capped at 15360, 16384 is the OS Limit) all in one server, Initially for guilds kwadratowamasakra.pl project.

It's crafted as a lightweight server, designed for handling tasks like logging in, verifying accounts, and more, without the overhead of managing worlds, etc.

Specifically designed for players playing Minecraft version 1.8-26.2 (protocols 47-776), it also lets you make custom plugins with custom commands and listeners.

Block/item mappings, dimension data and configuration registries are generated from
[PrismarineJS minecraft-data](https://github.com/PrismarineJS/minecraft-data). The 26.2 data set is supplied by
[Complexity-ML/minecraft-data-26.2](https://github.com/Complexity-ML/minecraft-data-26.2) until it is available upstream.
Vanilla tags can be generated from the matching official server JARs and their registry reports. For an exact
rebuild, pass a directory containing captured vanilla `configuration-<protocol>.bin` and `tags-<protocol>.bin`
packet bodies; this avoids relying on delayed or incomplete `minecraft-data` registry updates. Put the JARs in one
directory using names such as `1.20.5.jar`, `1.21.11.jar` and `26.2.jar`, then run:

```bash
node tools/generate-protocol-resources.mjs \
  /path/to/minecraft-data/data \
  /path/to/minecraft-data-26.2/data \
  /path/to/vanilla-server-jars \
  /path/to/captured-protocol-payloads
```

The generator replaces resources only after every required version succeeds. Packet IDs for the subset of packets
implemented by this server remain explicitly registered in `PacketManager`.

## How to run (Linux)

- Install Java 25

  - https://docs.aws.amazon.com/corretto/latest/corretto-25-ug/generic-linux-install.html
 
- Install screen
  - https://www.digitalocean.com/community/tutorials/how-to-install-and-use-screen-on-an-ubuntu-cloud-server

- Create a directory and place the server.jar file in it

- Create a start scripts (Example here):
	
	`start.sh`
	
	```bash
	screen -S Server1 ./restart.sh
	```
	
	`restart.sh`
	
	```bash
	#!/bin/bash
	while true; do
	
	    java -Xms768M -Xmx768M -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Djdk.http.auth.tunneling.disabledSchemes="" -jar server.jar nogui
	    sleep 5s
	
	done
	```
- Give permissions to your start scripts:
	```bash
 	chmod +x start.sh restart.sh
 	```
- Run your `start.sh` script
	```bash
	./start.sh
	```
