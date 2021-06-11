# Not Enough Packs
[Curseforge](https://www.curseforge.com/minecraft/mc-mods/nepacks)

[Changelog](CHANGELOG.md)

## Table of Contents
* [Overview](#about)
* [Config](#config)
* [Road Map](#roadmap)
* [Translations](#translations)
* [License](LICENSE)
* [Minecraft EULA](https://www.minecraft.net/en-us/eula/)
* [Fabric License](https://github.com/FabricMC/fabric-loader/blob/master/LICENSE)
* [Contributors](CONTRIBUTORS)

## About

This mod allows you to overcome some of the short comings of Fabric API's mod resource and datapack handling.

Have you ever wanted to do something like:
* Have an addon mod override a texture or block model of a parent mod
* Have an addon mod override a recipe of a parent mod

With Fabric API, you can't reliably do this. It creates one "big ball mud" resource/datapack for all mods.
There is no guarantee in which order they will be asked for files. 
In fact, the order is "random" because of the way it is implemented.

This mod "explodes" the resource and datapack out so that each individual mod is visible.
This allows you to order them how you like.

Additionally there is an option (turned off by default) to automatically order mods by dependency.
This involves more invasive changes to Fabric and Minecraft to preserve the ordering.

This mod does not change any of the other rules.
Once the resource pack list has been generated for the first time, the order is stored/retrieved from options.txt.
For data packs the order is stored in the saved data of the world.

Adding new mods after you have first generated these lists, will add the mod to the "top" of the list.

## Config
The configuration file nepacks.json can be found at mod-pack-root/config/nepacks/nepacks.json

* sortMods - whether to sort mods according to dependency (default false)

The actual algorthm used is:
* Sort all the mods into alphabetical order by mod id
* Go through that list and move any mods that have dependencies to just after the last mod it depends upon

This means that;
* For the same set of mods the ordering is deterministic (even if there are circular dependencies)
* An addon mod will be asked for a file before the parent it depends upon (unless there are circular dependencies)

## RoadMap

NOTE: This is just spitballing, there is no guarantee these are good ideas or will be implemented

* Add a button to the resource/data pack screen that lets you run the reorder calculation again (this would be useful after adding a mod or you messed up and want to restart)
* Make the data pack screen available in game, this would allow more user friendly ordering of datapacks for existing worlds than that provided by the datapack command
* Add a mechanism similar to the options.txt for datapacks where the order can be specified instead of being recalculated for every new world, this would be useful for modpack authors who know the order they want things

## Translations
To make your own translation, add a resource pack with an assets/nepacks/lang/xx_yy.json
<br>Please feel free to contribute back any translations you make.
<br>At time of writing this, there is no translatable content

[English](src/main/resources/assets/nepacks/lang/en_us.json)