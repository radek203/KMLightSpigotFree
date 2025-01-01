KMLightSpigot
===========

Warning: I do not take responsibility for the usage of this project in a production environment! It is only the public version of this project. You bear full responsibility for securing and configuring your lobby server, proxy server, etc.

## What is this project?

This server is built to handle thousands of players all in one server, Initially for guilds kwadratowamasakra.pl project.

It's crafted as a lightweight server, designed for handling tasks like logging in, verifying accounts, and more, without the overhead of managing worlds, etc.

Specifically designed for players playing Minecraft version 1.8.x, it also lets you make custom plugins with custom commands and listeners.

## How to run

-Install latest Java 21 version, and screen for your linux

-Create a directory and place the server jar file in it

-Create a run script:

-For testing on Windows, create a .bat file with the following content: java -jar server.jar.

-However, I strongly recommend running it on Linux, where you can utilize screen and scripts for auto-updating plugins, etc.

For example:

start.sh

```bash
screen -S Server1 ./restart.sh
```

restart.sh

```bash
#!/bin/bash
while true; do

    FILE=./plugins/yourPluginNew.jar
    if test -f "$FILE"; then
        cp -f ./plugins/yourPluginNew.jar ./plugins/yourPlugin.jar
        rm ./plugins/yourPluginNew.jar
    fi
    java -Xms768M -Xmx768M -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Djdk.http.auth.tunneling.disabledSchemes="" -jar server.jar nogui
    sleep 5s

done
```

Learn more about screen (how to run etc.) here: https://www.geeksforgeeks.org/screen-command-in-linux-with-examples/

## How to create a plugin? (Maven)

You can see the example (!!!) below with one command and one listener:

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>SamplePlugin</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <jar.finalName>${project.name}New</jar.finalName>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.3</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <createDependencyReducedPom>true</createDependencyReducedPom>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.1</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>org.example.Main</mainClass>
                        </manifest>
                        <manifestEntries>
                            <Plugin-Name>SamplePlugin</Plugin-Name>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>

    <dependencies>
        <dependency>
            <groupId>pl.kwadratowamasakra</groupId>
            <artifactId>lightspigot</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>system</scope>
            <systemPath>
                C:/.../server.jar
            </systemPath>
        </dependency>
    </dependencies>

</project>
```

Main.java

```java
package org.example;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.config.Configuration;

public class Main {

    public final void onEnable(final LightSpigotServer server, final Configuration conf, final FileHelper fileHelper) {
        new PlayerLoginListener(server);
        new SampleCommand(server);
    }

    public final void onDisable() {

    }
}
```

SampleCommand.java

```java
package org.example;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.command.Command;
import pl.kwadratowamasakra.lightspigot.command.CommandSender;

import java.util.List;

public class SampleCommand extends Command {

	public SampleCommand(final LightSpigotServer server) {
		super("sample", List.of("sample1", "sample2"));

		server.getCommandManager().addCommand(this);
	}

	@Override
	public final void handle(final CommandSender sender, final String[] args) {
		// handle command
	}

}
```

PlayerLoginListener.java

```java
package org.example;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.event.EventListener;
import pl.kwadratowamasakra.lightspigot.event.PlayerLoginEvent;

public class PlayerLoginListener implements EventListener<PlayerLoginEvent> {

    public PlayerLoginListener(final LightSpigotServer server) {
        server.getEventManager().addEvent(PlayerLoginEvent.class, this);
    }

    @Override
    public final void handle(final PlayerLoginEvent e) {
        // handle event
    }
}
```
