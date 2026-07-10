# Minecart Announcer 3.1

**Minecart Announcer** is a lightweight plugin designed for private Minecraft servers. It enables vanilla-friendly announcements for players traveling via **minecarts** or **boats**.

---

## Features

- Full color formatting support
- Player-specific **chat messages**
- Custom **bossbars**
- On-screen **titles & subtitles**
- **Directional triggering** (based on travel direction)
- Works with **minecarts** and **boats**

---

## Setup Instructions

### Minecarts

To set up a message trigger:

1. **Place a Rail on top of an Iron Block**  
   ![Minecart Setup](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/Setup.png)

2. Use a **Book and Quill** to write your message.

3. Once finished, **sign the book**, then **left-click** the Rail block (with Iron Block underneath) using the signed book.

If the setup is successful, you’ll see **green particles** and receive a private confirmation message in chat.

---

### Boats

1. **Place a Polished Blackstone Pressure Plate** on any type of Ice block.  
   ![Boat Setup](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/boatSetup.png)

2. Use the same method as with minecarts: write your message in a Book and Quill, sign it, then left-click the pressure plate with the signed book.

> _Note: Visible tripwires in the screenshot are from the **Vanilla Tweaks** resource pack and used for directional control._

---

## Chat Messages

By default, the content of the book will be sent to the **chat** of the player who triggered it.  
If you use special tags (see below), it will display as a bossbar or title instead.

---

## Color Formatting

You can use **Minecraft’s default color codes**, used inside a format expression **`$()`**  and written in **uppercase**.

Example: `$(C)Warning!` → displays as red text  
![Text Formatting](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/Minecraft_Formatting.webp)

---
## Functions
```
@FunctionName(Key:Value,...)"Content";
```

---
## Bossbars

To display your message as a **bossbar** you need to use bossbar function:

```
@Bossbar()"Your title $(B)with formating";
```
Bossbar function takes in following parameters:

| Parameter name | Parameter type | Default value | Description | Possible Inputs                                                                      | 
|--------------- |--------------| ------------- | ----------- |--------------------------------------------------------------------------------------|
| Color | Enum | WHITE | Sets bossbar color to one of minecraft colors | - RED <br/> - BLUE <br/>- PINK <br/>- GREEN <br/>- YELLOW <br/>- PURPLE <br/>- WHITE |
| Time | Int64 | 100 | Sets how long is bossbar on screen using Minecraft Tick time unit which is 1/20 of a second | Any Integer above 0 |
| Style | Enum | SOLID | Sets bossbar style to one of minecraft built in styles | - SOLID <br/>- DIV_6<br/>- DIV_10<br/>- DIV_12<br/>- DIV_20                          |

Bossbar remaining time in seconds can be displayed using `^TimeLeft` in text field after parameters

Example usage:
```
@Bossbar(Color:PINK,Style:DIV_10)"Your title dissapears after $(6)^TimeLeft sec";
```


---

## Titles & Subtitles

To show your message as an **on-screen title**, use title function:

```
@Title()"Your title $(4)with formating";
```

Title function takes in following parameters:

| Parameter name | Parameter type | Default value | Description | Possible Inputs                                                                      |
|--------------- |--------------| ------------- | ----------- |--------------------------------------------------------------------------------------|
| Subtitle | String | EMPTY | Minecraft subtitle as in /title comand | Any text - formating included |
| Time | Int32 | 60 | Sets how long is bossbar on screen using Minecraft Tick time unit which is 1/20 of a second | Any Integer above 0 |


Example usage:
```
@Title(Subtitle:"$(8)Departure at $(6)21:37")"Warsaw Central";
```

---

## Directional Triggering

### Minecarts

To make the message trigger **only when approaching from a specific direction**:

- Place a **Bone Block** beneath the previous Rail (from which the player will be coming).  
  Only players entering from that direction will see the message.

### Boats

- Place **String (Tripwire)** in the direction the player will approach from (including diagonals).  
  This ensures that players coming from the correct path trigger the pressure plate message.

---

## Example

**Setup:**  
![Example prepared message](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/example_message.png)

**In-game Result:**  
![Example player screen when running](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/example_playerscreen.png)

---

## Changelog

### 3.1.1
- Fixed bug where if you modified config message would be unreachable unless you turn it back
- Added safeguard for tampering with data in database
- Added new option in config.yml - `protection.enabled` (by default true), it prevents block destruction from someone without permission to edit messages

### 3.1
- Added permissions: `mcannouncer.dev`, `mcannouncer.edit_messages`, `mcannouncer.get_messages`
- Added config.yml so you can change predefined blocks
- Added `/announcer` command
- Moved `/dev_showMessagesPositions` to `/announcer dev get_message_positions`

Known Issues:
- You need to destroy exact 'Ice Activator' block to remove message

### 3.0
- Reworked Message handling from ground up, using now *Minezor Pages v1.0* (C# joke)
  → Removed this weird amalgamation of syntax
  → Added more flexible parameters
- Fixed issue where messages would overlap, now future messages are waiting in queue unless player leaves sever 
- Removed duplicate data from database
- Fixed error where Time was calculated wrong when DayLightCycle was disabled

### 2.0
- Switched storage from JSON to **SQLite**  
  → You can edit it with [DB Browser for SQLite](https://sqlitebrowser.org/)
- Bugfixes: `#0003`

### 1.3
- **Boats support** added

### 1.2.3
- Bugfixes: `#0001`, `#0002`
