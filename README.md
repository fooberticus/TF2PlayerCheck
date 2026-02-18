# TF2 Player Check
(formerly [SteamTools](https://github.com/fooberticus/SteamTools))

Easy-to-use tools for checking Team Fortress 2 (TF2) players for VAC and community server bans. Know who you're playing with!

This tool takes console output from the `status` command to check several different sources for player bans and other information, including:

* Steam VAC and Game bans from Valve
* Steam Community Server bans and other info from SteamHistory.net
* Rent-a-Medic cheater lists
* Backpack.tf TF2 inventory values
* Time on server, ping, Steam ID and more

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/10-right-mouse-1.png)

## Modern look and feel

TF2PlayerCheck uses Flatlaf, with over a dozen popular themes for you to choose to customize your UI.

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/theme-1.png)

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/theme-2.png)

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/theme-3.png)

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/theme-4.png)

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/theme-5.png)

## Installation and Enabling TF2 Developer Console

On Windows you can download a Zip or MSI installer from the releases section. Either install using the MSI, or unzip 
where you wish to install. Run the .exe and the program will start. These Windows distributions come with a Java Runtime Environment (JRE) included.

On Linux you can download the .jar from the releases section and run it with `java -jar <name of .jar file>`. Requires Java 25.

If you're a Java developer, you can clone this git project and run `mvn exec:java` in the project root. Requires Java 25 and Maven 3.

In your TF2 game, be sure the developer console is enabled. This is where you'll run the `status` command to get information
about who is playing on the server. When the status output is displayed, copy and paste it into the text field in the program.

For more information about enabling the developer console, see the [TF2 Wiki](https://wiki.teamfortress.com/wiki/Console).

## Setup: Configuring API Keys

The first time you run the program, you'll be presented with the API Key Configuration menu:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/1-key-config.png)

To use TF2PlayerCheck, you'll need API keys for the Steam API, SteamHistory.net API and Backpack.tf API.

You can get a Steam API key here:

https://steamcommunity.com/dev/apikey

For SteamHistory.net, you can login to the site with your Steam account and then get an API key here:

https://steamhistory.net/api

And for Backpack.tf, you can login to the site with your Steam account and then get an API key here 
(be sure to copy the API Key and not the Access Token):

https://next.backpack.tf/account/api-access

Once you've copied and pasted the API keys into the appropriate fields, you can save and then close the settings menu.
You're ready to use TF2 Player Check!

You can also adjust the appearance of the application using the Appearance Configuration window:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/2-appearance-config.png)

You can access both of these configuration windows at any time from the Settings menu in the main window:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/3-settings-menu.png)

## Usage

First, in Team Fortress 2, open the console and run the `status` command. Highlight the output text and copy it:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/4-console.png)

You don't have to be precise, just make sure you copy whatever the `status` command prints out.

This is the main window:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/5-main-window.png)

The text field in this window is where you paste the output of the `status` command. Either hit `Ctrl+V` or right-click to paste.
Right-clicking clears the text field and pastes automatically, to make using the tool a little easier:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/6-after-paste.png)

Next, click the Check Users button and wait for the Results window to open.

If any players have a VAC or Steam Game ban associated, the Steam Bans tab will appear with their name and
some info about the bans. You can double-click a row of player data to open their Steam profile:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/8-steam-bans.png)

Likewise, if any players have community server bans, the Community Bans tab will be present with the player's name and ban summary.
You can double-click a row of player data to open their page on SteamHistory.net:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/7-community-bans.png)

Finally, on the All Players tab you'll see a summary of all players on the server, whether they have a ban or not. 
This screen also shows general information about each player, like ping and time on server, as well as their Backpack.tf inventory value.

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/9-all-players.png)

Right-click on any row in any screen to open a context menu where you can open a player's Steam Profile, SteamHistory.net profile, Backpack.tf inventory,
and more. You can also copy the player's name, Steam ID and any URLs to sources of information used by this app.

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/10-right-mouse-1.png)

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/11-right-mouse-2.png)

## Advanced Usage: Importing from Files, JSON and More

In addition to copying and pasting the output of `status`, you can also use any text containing Steam IDs. TF2PlayerCheck can parse Steam IDs from just about anything.

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/12-json-text.png)

You can also import a file directly into the text input area using the File → Import menu option. 

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/13-file-menu.png)

This is handy if you want to process a large console.log file, or you have a json file full of Steam IDs for instance.

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/14-file-chooser.png)

The various APIs TF2PlayerCheck uses have request size limits of 100 Steam IDs per request, but have no fear. 
Lists of Steam IDs larger than that will be processed in multiple smaller batches. So if you have a console.log file that you've been accumulating 
for a while that has thousands of IDs in it, go ahead and import it. Just be prepared to wait a bit while all the API calls are made.

## Build and Package the Project

If you're a Java developer, you can build the project with Maven:

`mvn clean package`

If you want to use `jpackage` to make an MSI and package up the JVM with a native executable, use the scripts found in the "jpackage"
folder in the root of the project. Just copy the *jar-with-dependencies.jar into the `jpackage\jars` folder, update the version
information in the files in the jpackage\opt folder, then run the batch script named `package-windows.bat` to create the MSI and Package with executable.
