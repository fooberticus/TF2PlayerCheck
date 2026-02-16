# TF2 Player Check

Easy to use tools for checking Team Fortress 2 (TF2) players for VAC and community server bans. Know who you're playing with!

This tool take console output from the `status` command to check several different sources for player bans and other information, including:

* Steam VAC and Game bans from Valve
* Steam Community Server bans and other info from SteamHistory.net
* Rent-a-Medic cheater lists
* Backpack.tf TF2 inventory values
* Time on server, ping, Steam ID and more

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/all-players.png)

# Setup

On Windows you can download a Zip or MSI installer for Windows from the releases section. Either install using the MSI, or unzip 
where you wish to install. Run the .exe and the program will start.

On Linux you can download the .jar from the releases section and run it with `java -jar <name of .jar file>`. Requires Java 25.

If you're a Java developer, you can clone this git project and run `mvn exec:java` in the project root. Requires Maven 3.

In your TF2 game, be sure that the developer console is enabled. This is where you'll run the `status` command to get information
about who is playing on the server!

# Configuring API Keys

The first time you run the program, you'll be presented with the Settings menu:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/settings.png)

The first thing you need are API keys for the Steam API, SteamHistory.net API and Backpack.tf API.

You can get a Steam API key here:

https://steamcommunity.com/dev/apikey

For SteamHistory.net, you can login to the site with your Steam account and then get an API key here:

https://steamhistory.net/api

And for Backpack.tf, you can login to the site with your Steam account and then get an API key here 
(be sure to copy the API Key and not the Access Token):

https://next.backpack.tf/account/api-access

Once you've copied and pasted the API keys into the appropriate fields, you can save and then close the settings menu.
You're ready to use TF2 Player Check!

You can also increase/decrease the global font size and change the UI theme from this menu. Play around with the settings
until you find a look and feel that you like best.

# Usage

This is the main window:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/empty-main.png)

In the text field of this window, you can input any number of Steam32 IDs -- Steam IDs that look like [U:1:12345678].
The most common way to use this tool is to open the console while playing TF2, type in the command `status`, then copy the
text that comes out and paste it into this text box (you can **right-click to paste** into this window to save time):

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/pasted-main.png)

The text you copy and paste doesn't have to be precise, just make sure you copy the output of the status command. Extra text is ignored.

Next, click the Check Users button and wait a second or two, and the results of the Steam and community ban check will come back.

If any of the checked Steam IDs have a VAC or Steam Game ban associated, the Steam Bans tab will appear with their name and
some info about their bans shown. You can double-click a row of player data to open their Steam profile page and have a closer look:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/steam-bans.png)

Likewise, if any Steam IDs have community server bans, the Community Bans tab will be present with the player's name and ban summary.
Again, you can double-click a row of player data to open their page on SteamHistory.net and see more details about all their community bans:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/community-bans.png)

Finally, on the All Players tab you'll see a summary of all players on the server, whether they have a ban or not. Double click
their row to open their Steam Profile to have a closer look:

![](https://github.com/fooberticus/TF2PlayerCheck/blob/main/img/all-players.png)

You can right on any row in any screen to open a player's Steam Profile, SteamHistory.net profile, Backpack.tf inventory,
and more. You can also copy the player's name, Steam ID and any URLs to sources of information used by this app.

And that's it for now. More features are being added, but as of now this is a handy way to quickly find out if you are playing
with anyone who has a known record of cheating in TF2!

# Build and package the project

If you're a Java developer, you can build the project with Maven:

`mvn clean package`

If you want to use `jpackage` to make an MSI and package up the JVM with a native executable, use the scripts found in the "jpackage"
folder in the root of the project. Just copy the *jar-with-dependencies.jar into the `jpackage\jars` folder, update the version
information in the files in the jpackage\opt folder, then run the batch script named `package-windows.bat` to create the MSI and Package with executable.