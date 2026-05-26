---
title: "The Points System"
sidebar_position: 8
---

Points are similar to currencies, however they exist purely as a way to keep track of something in a player. For example, lets say you want the player to have to used an item a certain amount of times in order to use another one - you would be able to keep track of this with a point.

You can have as many different points as you want, the plugins will keep track of them automatically. Points are shared between plugins, too - so if you make a point in EcoItems, then you can use it in EcoPets, EcoJobs, etc.

A point can hold any numeric value, including negatives and decimals, but of course how you decide to use them is completely up to you.

## Point Types

| Point Type    | Placeholder                          | Description                                                                                                                                                                                                                                         |
| ------------- | ------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Points        | `%libreforge_points_<point>%`        | A general point that can be used to keep track of something per-player. (e.g. times jumped, zombies killed, etc.) You can also use this points within [Prices](https://plugins.auxilor.io/all-plugins/prices), to easily create cost based systems. |
| Global Points | `%libreforge_global_points_<point>%` | A general point that keeps track of something globally/server-wide for all players. (e.g. zombies killed by all players.)                                                                                                                           |
| Item Points   | `%libreforge_item_points_<point>%`   | A point that is tied to a specific item and tracks data on the item. Useful for making tools with custom durability's or per-item stats (e.g zombies killed using this sword.)                                                                      |

## Commands
| Command                                               | Description                            | Permission                        |
|-------------------------------------------------------|----------------------------------------|-----------------------------------|
| `/libreforge points set <player> <point_id> <value>`  | Set points value for the player/global | `libreforge.command.points.set`   |
| `/libreforge points give <player> <point_id> <value>` | Give points to the player/global       | `libreforge.command.points.give`  |
| `/libreforge points take <player> <point_id> <value>` | Take points from the player/global     | `libreforge.command.points.take`  |
| `/libreforge points get <player> <point_id>`          | Gets the balance/value of points       | `libreforge.command.points.get`   |
| `/libreforge points reset <player> <point_id>`        | Reset the point value back to 0        | `libreforge.command.points.reset` |

To change global points, pass `global` as the player name.

## Effects System
Points are seamlessly integrated into the effects' system. Below, you'll find a list of all available effects and conditions.

| Effect                   | Type      | Link                                                                          |
| ------------------------ | --------- | ----------------------------------------------------------------------------- |
| `add_global_points`      | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/add_global_points)      |
| `add_points`             | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/add_points)             |
| `give_global_points`     | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/give_global_points)     |
| `give_item_points`       | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/give_item_points)       |
| `give_points`            | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/give_points)            |
| `multiply_global_points` | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/multiply_global_points) |
| `multiply_item_points`   | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/multiply_item_points)   |
| `multiply_points`        | Effect    | [Link](https://plugins.auxilor.io/effects/all-effects/multiply_points)        |
| `above_global_points`    | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/above_global_points) |
| `below_global_points`    | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/below_global_points) |
| `global_points_equal`    | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/global_points_equal) |
| `above_points`           | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/above_points)        |
| `below_points`           | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/below_points)        |
| `points_equal`           | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/points_equal)        |
| `item_points_above`      | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/item_points_above)   |
| `item_points_below`      | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/item_points_below)   |
| `item_points_equal`      | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/item_points_equal)   |


