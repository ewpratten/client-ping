# Client Ping

**Client Ping** is a client-side Minecraft mod that implements "pinging" (marking an object as a point of interest) on vanilla servers.

## Usage

By default, **Client Ping** uses the <kbd>z</kbd> key to ping the block you are looking at (within 1000m). You can change this key bind in the Minecraft controls menu.

Pings will be visible for a short time to yourself and all players on the same server with this mod installed.

- Pings disappear after 10 seconds
- You may ping once per second

## How it works

**Client Ping** works by sending pings through vanilla server chat. This way, even players without the mod installed can see the coordinate you have pinged. No more copying from the <kbd>F3</kbd> menu!

Chat messages come in the following format:

```text
<Player> Ping at {x.xx, y.yy, z.zz}
```

When a client receives and parses one of these ping messages, a temporary waypoint is injected into [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap). This allows the ping mark to be rendered on screen according to your existing waypoint rendering settings. It'll also be marked on your map if you have [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) installed.
