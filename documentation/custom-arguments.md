---
title: "Custom Arguments"
sidebar_position: 12
---

You can create custom effect arguments to reuse common logic between
different effects. This is useful if you want to have more in-depth messages or
add sounds to arguments to make them more immersive.

## How to make a Custom Argument

Like Item Levels, Custom Arguments are each config files placed in the `/plugins/libreforge/arguments/` folder, and you can add or remove them as you please.

There's an example config called `_example.yml` to help you out!

## `_example.yml`

```yaml
# The ID of the argument is the name of the .yml file, so for example
# named_mana.yml would have an ID of named_mana.

# Conditions to check if the argument is met
is-met:
  - id: above_magic
    args:
      type: mana
      amount: "%amount%"

# Effects to run if the condition is met
if-met:
  - id: give_magic
    args:
      type: mana
      amount: "- %amount%"
  - id: send_message
    args:
      message: "-%amount% %ecoskills_mana_name% &f(%reason%)"

# Effects to run if the condition is not met
if-not-met: [ ]
```

## Using a Custom Argument

Custom arguments work just like regular arguments in your effect configs, but with
a `custom_` prefix. For example, if you have an argument called `named_mana`, you could
use it like this:

```yaml
args:
  custom_named_mana:
    amount: 10
    reason: Instant Transmission
```

And then in your argument config, you can use `%amount%` and `%reason%` in the condition
and effect configs to get their values.
