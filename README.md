# ItemCollection

![Version](https://img.shields.io/badge/Version-1.0-brightgreen.svg)
![Spigot](https://img.shields.io/badge/Spigot-1.21%2B-blue.svg)

**ItemCollection** is a high-performance, Hypixel-style collection system framework intended for modern Minecraft servers. 
It securely natively saves player data instantly over the Bukkit `PersistentDataContainer` (NBT) offering robust scalability without external database prerequisites.

## ✨ Features
* **NBT-Based Storage**: Progress binds locally directly onto player data for maximum I/O performance.
* **Custom Interactive GUI**: An aesthetically rich 54-slot visual menu featuring responsive player progression paths and stylized progress bars natively (`[|||||-----]`).
* **Admin Integrations**: Force overwrite and debug player integers natively via in-game permissive commands.
* **PlaceholderAPI Support**: Seamless integration hooking capability readily built into the infrastructure for expansions in external plugins like DeluxeMenus, Scoreboards, or TAB!
* **Optimized Event Calling**: Handled rigorously on `EventPriority.MONITOR` checking `Creative` modes specifically to safeguard fairness. 

## 📦 Installation

Installing ItemCollection is straightforward:
1. Download the latest `ItemCollection.jar` release.
2. Navigate and drop the jar directly into your server's `/plugins` folder.
3. Restart your server to initialize the dynamic keys and data correctly.

## ⌨️ Commands & Permissions

### Player Commands
* `/collection view` - Opens the main collection visual UI hub.
* `/collection stats <material>` - Whispers a direct message describing raw block counts locally to your chat.

### Admin Commands
Requires permission node: `collection.admin`
* `/collection add <player> <material> <amount>` - Binds extra integers forcibly to a player’s block NBT count.
* `/collection reset <player> <material>` - Erases all existing progress formatting for that material on the target entirely.

## 📝 License
This project is licensed under the [MIT License](LICENSE) - see the LICENSE file for details.

## ✍️ Author
Designed and Engineered by **LokayaFx**.
