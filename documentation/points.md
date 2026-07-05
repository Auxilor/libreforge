---
title: "The Points System"
sidebar_position: 12
---

Points are a way to track a number against a player, similar to a currency but used purely for keeping count. For example, if you want a player to have used one item a certain number of times before they can use another, a point keeps track of that. This page explains the **point types**, the **commands** to manage them, and the **effects and conditions** that read and write them.

You can have as many points as you want; the plugins track them automatically. Points are shared between plugins, so a point made in EcoItems can be used in EcoPets, EcoJobs, and so on. A point can hold any numeric value, including negatives and decimals.

## Point types

| Point type | Placeholder | Description |
| --- | --- | --- |
| **Points** | `%libreforge_points_<point>%` | A general per-player point (e.g. times jumped, zombies killed). You can also use these within [Prices](https://plugins.auxilor.io/all-plugins/prices) to build cost-based systems. |
| **Global points** | `%libreforge_global_points_<point>%` | A point tracked globally for all players (e.g. zombies killed by everyone). |
| **Item points** | `%libreforge_item_points_<point>%` | A point tied to a specific item, tracking data on that item. Useful for custom durability or per-item stats (e.g. zombies killed with this sword). |

## Commands

To change global points, pass `global` as the player name.

| Command | Description | Permission |
| --- | --- | --- |
| `/libreforge points set <player> <point_id> <value>` | Set the points value for the player or global | `libreforge.command.points.set` |
| `/libreforge points give <player> <point_id> <value>` | Give points to the player or global | `libreforge.command.points.give` |
| `/libreforge points take <player> <point_id> <value>` | Take points from the player or global | `libreforge.command.points.take` |
| `/libreforge points get <player> <point_id>` | Get the balance of points | `libreforge.command.points.get` |
| `/libreforge points reset <player> <point_id>` | Reset the point value back to 0 | `libreforge.command.points.reset` |

## Effects and conditions

Points are integrated into the effects system. Below are all the effects and conditions for working with them.

| Name | Type | Link |
| --- | --- | --- |
| `add_global_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/add_global_points) |
| `add_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/add_points) |
| `give_global_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/give_global_points) |
| `give_item_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/give_item_points) |
| `give_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/give_points) |
| `multiply_global_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/multiply_global_points) |
| `multiply_item_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/multiply_item_points) |
| `multiply_points` | Effect | [Link](https://plugins.auxilor.io/effects/all-effects/multiply_points) |
| `above_global_points` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/above_global_points) |
| `below_global_points` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/below_global_points) |
| `global_points_equal` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/global_points_equal) |
| `above_points` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/above_points) |
| `below_points` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/below_points) |
| `points_equal` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/points_equal) |
| `item_points_above` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/item_points_above) |
| `item_points_below` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/item_points_below) |
| `item_points_equal` | Condition | [Link](https://plugins.auxilor.io/effects/all-conditions/item_points_equal) |

<hr/>

## Where to go next

- **Effects:** [Configuring an Effect](configuring-an-effect) to wire points into triggered or permanent effects.
- **Prices:** [Prices](https://plugins.auxilor.io/all-plugins/prices) to use points as a currency.
- **Item levels:** [Item Levels](item-levels) for the related per-item data system.
