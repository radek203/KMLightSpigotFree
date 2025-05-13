KMLightSpigot
===========

Warning: I do not take responsibility for the usage of this project in a production environment! It is only the public version of this project. You bear full responsibility for securing and configuring your lobby server, proxy server, etc.

## What is this project?

This server is built to handle thousands of players all in one server, Initially for guilds kwadratowamasakra.pl project.

It's crafted as a lightweight server, designed for handling tasks like logging in, verifying accounts, and more, without the overhead of managing worlds, etc.

Specifically designed for players playing Minecraft version 1.8.x and 1.12.2, it also lets you make custom plugins with custom commands and listeners.

## How to run (Linux)

- Install Java 21

  - https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/generic-linux-install.html
 
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
