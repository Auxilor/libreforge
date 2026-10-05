---
title: "Glossary"
sidebar_position: 1
---

This page gives the meaning of each term that the developer pages use. Read this page first if a term is new to you.

## Terms

| Term | Meaning |
| --- | --- |
| **Element** | A part that you can use in a config: an effect, a trigger, a condition, a filter, or a mutator. |
| **Effect** | The action that libreforge does. For example, it gives money or stops fall damage. |
| **Permanent effect** | An effect that stays on while its conditions are true. It has no trigger. |
| **Triggered effect** | An effect that runs one time each time its trigger occurs. |
| **Trigger** | An event that makes triggered effects run. For example, a player jumps. |
| **Condition** | A check that must be true before an effect can run or stay on. |
| **Filter** | A check on the data of one trigger. For example, "only when the block is diamond ore". |
| **Mutator** | A change to the data of one trigger before the effect gets it. For example, it moves the location. |
| **Holder** | An object that contains a list of effects and a list of conditions. Examples are a custom item, an enchantment, or a pet. |
| **Provided holder** | A holder together with the object that gave it to the player. For example, a holder and the item stack that has it. |
| **Holder provider** | Your code that tells libreforge which holders a dispatcher has now. |
| **Dispatcher** | The thing that has holders and that effects act on. Usually this is a player. It can also be a mob, a block, a location, or the server. |
| **Trigger data** | The values that a trigger sends to effects: the player, the victim, the block, the location, the item, the event, and two numbers. |
| **Trigger parameter** | One value in the trigger data that a trigger promises to send. For example, `PLAYER` or `BLOCK`. |
| **Compile data** | Data that you calculate one time when the config loads, not each time the element runs. Use `NoCompileData` if you do not need it. |
| **Identifiers** | A UUID and a key that are unique to one permanent effect on one dispatcher. Use them to find the changes that the effect made, so you can remove them. |
| **Registry** | The list where libreforge keeps each element type. Libreforge only finds elements that you register. |
| **ID** | The name that users write in the config to use your element. It must be unique and in `snake_case`. |
| **Arguments** | The settings that a user writes under `args` in the config. |

## Where each element runs

```
Trigger occurs
  -> libreforge finds the active effects of the dispatcher
  -> mutators change the trigger data
  -> filters check the trigger data
  -> conditions check the dispatcher
  -> the effect runs
```

Permanent effects do not use this flow. Libreforge turns them on when the holder and its conditions are present. Libreforge turns them off when the holder or a condition is not present.
