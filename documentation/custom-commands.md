---
title: "Custom Commands"
sidebar_position: 11
---

## Creating a Custom Command

Each command goes into the `commands.yml` file, and you can add or remove them as you please.

Commands require an ID, this is specified in the command config, and is used in permissions and to differentiate between commands.

ID's must be lowercase letters, numbers, and underscores only.

## Example Command Config

```yaml
commands:
  - id: "example_command"
    alias: "examplecmd <player> <value>"
    permission: "libreforge.example_command"
    effects:
      - id: give_money
        args:
          amount: "%value%"
        conditions:
          - id: is_night
```
## Understanding all the sections

### The Command Info Section

```yaml
id: "example_command" # The command ID, used to differentiate commands. Used in permissions.
alias: "examplecmd <player> <value>" # The command alias, this is what players will actually type in-game to run the command.
permission: "libreforge.example_command" # The permission required to run the command (optional)
```

### The Command Effects Section
These are the effects that are run when the command is successfully sent.
```yaml
effects:
  - id: give_money
    args:
      amount: "%value%"
    conditions:
      - id: is_night
```
The effects section is the core functionality of the command. You can configure effects, conditions, filters, mutators and triggers in this section to run when the command is successfully sent.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

## Command Arguments

Within the alias, you can use argument tokens (e.g. `<player>`, `<value>`) which can then be used in the effects as placeholders (e.g. `%player%`, `%value%`).
You can use:
- `<player>`: Required player argument
- `<value>`: Required value argument
- `[value]`: Optional value argument

Within these arguments, you can use placeholders, such as `%player%` or even the `%trigger_value%` placeholders to use information from a trigger (when use in the `run_command` effect)