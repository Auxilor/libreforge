---
title: "Custom Commands"
sidebar_position: 11
---

Custom commands let you register an in-game command that runs a set of **effects** when a player types it. Each command has an **ID**, an **alias** players type, an optional **permission**, and the effects to run. This page covers creating one, naming it, its structure, and the argument tokens you can pass through to your effects.

## Quick start

1. Open the `commands.yml` file in `/plugins/libreforge`.
2. Add a command under `commands:` with an `id`, an `alias`, and a `permission`.
3. Add the effects to run under `effects:`.
4. Run `/libreforge reload`.
5. Type the alias in-game and confirm the effects run.

## Naming and IDs

Every command needs an `id`, set in the command config. It is used in permissions and to tell commands apart. It is separate from the `alias`, which is what players actually type.

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the command will not load.
:::

## The structure of a command

| Part | What it controls |
| --- | --- |
| **Command info** | The ID, the alias players type, and the permission |
| **Effects** | What runs when the command is used |
| **Arguments** | Tokens in the alias passed through to the effects |

```yaml
commands:
  - id: "example_command" # === Command info ===
    alias: "examplecmd <player> <value>" # What players type; <player> and <value> are arguments
    permission: "libreforge.example_command" # Optional; the permission required to run the command
    effects: # === Effects: what runs when the command is used ===
      - id: give_money
        args:
          amount: "%value%" # %value% comes from the <value> argument in the alias
        conditions:
          - id: is_night
```

### Command info

The `id`, `alias`, and `permission` identify the command and control who can run it.

```yaml
id: "example_command" # The command ID, used in permissions and to tell commands apart
alias: "examplecmd <player> <value>" # What players type in-game to run the command
permission: "libreforge.example_command" # Optional; the permission required to run the command
```

### Effects

These effects run when the command is used successfully. This is the core of the command: you can configure effects, conditions, filters, mutators, and triggers here, and even chains to string several effects together.

```yaml
effects:
  - id: give_money
    args:
      amount: "%value%"
    conditions:
      - id: is_night
```

:::danger Effects are their own system
Effects are configured the same way everywhere in libreforge, so they are documented separately.

- [Configuring an Effect](configuring-an-effect)
- [Configuring an Effect Chain](configuring-a-chain)
:::

### Arguments

In the alias you can use argument tokens, which become placeholders in your effects. For example, `<value>` in the alias is available as `%value%` in the effects.

| Token | Meaning |
| --- | --- |
| `<player>` | Required player argument |
| `<value>` | Required value argument |
| `[value]` | Optional value argument |

Inside these arguments you can use placeholders such as `%player%`, or `%trigger_value%` placeholders to pull information from a trigger when used with the `run_command` effect.

:::tip Troubleshooting
- **Command not loading?** Check the `id` is lowercase letters, numbers, and underscores only.
- **"No permission" in-game?** Grant the `permission` set on the command, or remove the `permission` line to make it open to everyone.
- **`%value%` showing as raw text?** Make sure the matching token (e.g. `<value>`) is present in the `alias`.
:::

<hr/>

## Where to go next

- **Effects:** [Configuring an Effect](configuring-an-effect) to build the effects your command runs.
- **Chains:** [Configuring an Effect Chain](configuring-a-chain) to group several effects under one command.
- **Placeholders:** [Custom Placeholders](custom-placeholders) to reuse values in your command effects.
