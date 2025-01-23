# JIRSend
<div style="text-align:center">
<img src="JIRSendApp/assets/jirsend_logo.png" width="100"><br><br>
<img alt="Ubuntu build" src="https://github.com/MagicTINTIN/JIRSend/actions/workflows/ubuntu-latest.yml/badge.svg">
<img alt="Windows build" src="https://github.com/MagicTINTIN/JIRSend/actions/workflows/windows-latest.yml/badge.svg">
</div>


## What is JIRSend ?
JIRSend is a decentralized chatsystem working on local network.\
The goal it to allow every user on a local network to chat with each other, but without a central server.\
For this purpose, JIRSend must detect automatically all users on the network so that any user can know who is connected.

### Functionalities

Here is a non-exaustive list of JIRSend features:
- A pretty Graphical User Interface (GUI)
- A pretty Command Line Interface (CLI) allowing to use JIRSend without a X11 Display (like most ssh connections)
- Discover other users on the network
- Send any text-message to anyone connected
- Autocompletion and history in the CLI!
- A Modding API and Loader, allowing to create mods
- A Mod to communicate outside the LAN
- A Mod to communicate with the ChatSystem of Jean-Philippe and Aurélien
- And a lot more!

## INSTALLATION (WITH NO MOD)

> [!IMPORTANT]
> If you want to use mods, you must build and install them first!\
> For that, please refer to the [mod section](#to-build-mods)

### No compilation needed:

1. go to the [releases section](https://github.com/insa-4ir-chatsystem/chatsystem-lasserre-servieres/releases)
2. download the latest release

### Build from source

```bash
git clone git@github.com:insa-4ir-chatsystem/chatsystem-lasserre-servieres.git
cd chatsystem-lasserre-servieres
mvn clean install
```
This should create a .jar file *`JIRSendApp/target/JIRSendApplication-jar-with-dependencies.jar`*.

## How to run the project

> [!IMPORTANT]
> First, JIRSend will launch differently depending of it's arguments.\
> If you want to launch as a Command Line Interface, use the *--cli* argument.\
> If you are debugging, use the *--debug* argument.

### Run JIRSend with GUI
Use one of the following methods:
- Double-click on the previously downloaded/built .jar file
- Enter the folowing command:
```bash
java -jar path-to-file.jar
```

### Run JIRSend with CLI

Enter the following command:
```bash
java -jar path-to-file.jar --cli
```

## To build mods

### Install JIRSendAPI
Install the dependency JIRSendAPI.

```sh
mvn clean install
```

### Build the mod
Then go in the directory of the mod you want to build and execute
```sh
mvn clean package
```

### Install the mod
Copy the `target/*-jar-with-dependencies.jar` into your `mods/`.\
Launch the app.\
You will see a button `Mods`.\
<img src="wiki/modsButton.jpg" width="30%">\
Click the button, you should see your mod in the list.\
<img src="wiki/loadedMods.jpg" width="40%">

## Project Overview

<div style="text-align:center">
<img src="wiki/connection.png" width="45%">
<img src="wiki/connection-cli-vscode.png" width="45%"><br>
</div>
<div style="text-align:center">
<img src="wiki/SixClients.png" width="100%"><br>
</div>
<br>
<div style="text-align:center">
<img src="wiki/2CLI-2GUI.png" width="100%"><br>
</div>

---

## To create mods

### Import in Maven
Once you installed the JIRSendAPI dependency, you will be able to import it in your Maven configuration
```xml
<dependency>
    <groupId>com.JIRSend</groupId>
    <artifactId>JIRSendAPI</artifactId>
    <version>1.0-SNAPSHOT</version> <!--you might check the version-->
</dependency>
```

### Implement JIRSendMod.java interface
The easiest way might be to look at the existing source of the mods in `mods/`

### Build
Export your .jar, put it in `mods/`, enjoy !\
<img src="wiki/modloader.png" width="70%">
