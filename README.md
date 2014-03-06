RedNet
======

An attempt to isolate the RedNet Cable from MineFactory Reloaded. This is a fork from MTlabs repo with changes to bring the code up to date with Minecraft 1.7.2.

I'm uploading these changes so it might benefit someone else. Hopefully the MFR folks will be able to use this to help update their mod to Minecraft 1.7.x.

To use, clone this repo into the src/main directory of your Forge 1.7.2-10.12.0.1034 dev environment.

## IDE Setup
* Download the 1.7.x version of Forge (10.12.0.x)
	* http://files.minecraftforge.net/
* Clone this repo into the src/main directory inside Forge
* Download Eclipse
	* https://www.eclipse.org/downloads/
* Open the Forge directory and run the gradle tasks for setting up the IDE
	* cd /path/to/forge
	* gradlew setupDevWorkspace eclipse
* Point Eclipse at the new workspace