---
title: "Custom Arguments"
sidebar_position: 12
---

Custom arguments let you bundle a **condition** and a set of **effects** into a reusable effect argument, so you can share common logic between effects. They are handy for richer messages or adding sounds to make actions more immersive. Each custom argument is a config file, and you call it with a `custom_` prefix. This page covers creating one, naming it, its structure, and how to use it.

## Quick start

1. Open the `/plugins/libreforge/arguments/` folder.
2. Copy `_example.yml` and rename it, e.g. `named_mana.yml`. The file name (without `.yml`) is the argument's ID.
3. Set the `is-met` conditions and the `if-met` / `if-not-met` effects.
4. Run `/libreforge reload`.
5. Add `custom_named_mana:` to an effect's `args`, then trigger that effect and confirm the argument's effects run.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real argument. You can also organise arguments into subfolders inside `arguments/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the argument's ID. A file named `named_mana.yml` has the ID `named_mana`, which you call as `custom_named_mana` inside an effect's `args`.

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the argument will not load.
:::

## The structure of a custom argument

| Part | What it controls |
| --- | --- |
| **is-met** | The conditions that decide which effects run |
| **if-met / if-not-met** | The effects to run for each outcome |
| **Using the argument** | How you call it from an effect |

```yaml
# === is-met: the conditions to check ===
is-met:
  - id: above_magic
    args:
      type: mana
      amount: "%amount%" # %amount% comes from where the argument is called

# === if-met / if-not-met: the effects for each outcome ===
if-met: # Runs when is-met passes
  - id: give_magic
    args:
      type: mana
      amount: "- %amount%"
  - id: send_message
    args:
      message: "-%amount% %ecoskills_mana_name% &f(%reason%)" # %reason% also comes from the call
if-not-met: [ ] # Runs when is-met fails; leave empty for nothing
```

### is-met

The conditions that decide whether the argument's `if-met` or `if-not-met` effects run. These are configured exactly like any other conditions.

```yaml
is-met:
  - id: above_magic
    args:
      type: mana
      amount: "%amount%" # Values passed in at the call site are available as placeholders
```

### if-met / if-not-met

The effects to run depending on the outcome of `is-met`. Leave `if-not-met` as `[ ]` to do nothing when the condition fails.

```yaml
if-met: # Effects to run when the condition is met
  - id: give_magic
    args:
      type: mana
      amount: "- %amount%"
if-not-met: [ ] # Effects to run when the condition is not met
```

:::danger Effects are their own system
The `if-met` and `if-not-met` effects, and the `is-met` conditions, are configured the same way everywhere in libreforge, so they are documented separately.

- [Configuring an Effect](configuring-an-effect)
- [Configuring an Effect Chain](configuring-a-chain)
:::

### Using the argument

Call a custom argument like a normal argument, but with a `custom_` prefix. Any keys you nest under it become placeholders (`%amount%`, `%reason%`, etc.) inside the argument's conditions and effects.

```yaml
args:
  custom_named_mana: # Calls arguments/named_mana.yml
    amount: 10 # Available as %amount% inside the argument
    reason: Instant Transmission # Available as %reason% inside the argument
```

:::tip Troubleshooting
- **Argument not running?** Check the file name is lowercase letters, numbers, and underscores only, and that the call uses the `custom_` prefix plus the exact file name.
- **Placeholders empty inside the argument?** Make sure each key you reference (e.g. `%amount%`) is nested under the `custom_<id>:` call.
- **Nothing happens when the condition fails?** That is expected if `if-not-met` is `[ ]`; add effects there to give feedback.
:::

<hr/>

## Where to go next

- **Effects:** [Configuring an Effect](configuring-an-effect) for the conditions and effects inside an argument.
- **Chains:** [Configuring an Effect Chain](configuring-a-chain) for another way to reuse complex logic.
- **Placeholders:** [Custom Placeholders](custom-placeholders) to share values across configs.
