---
title: "Custom Placeholders"
sidebar_position: 10
---

Custom placeholders let you reuse a **value**, a **math expression**, or a **conditional** lookup, and share data between plugins. You define them in one file and reference them as `%libreforge_<id>%`, with full PlaceholderAPI support. This page covers creating one, naming it, and the three kinds of placeholder you can build.

## Quick start

1. Open the `placeholders.yml` file in `/plugins/libreforge`.
2. Add a placeholder under `placeholders:` with an `id` and a `value`.
3. Run `/libreforge reload`.
4. Reference it anywhere as `%libreforge_<id>%` and confirm it resolves to your value.

## Naming and IDs

Each placeholder needs an `id`. You reference it as `%libreforge_<id>%`, so a placeholder with `id: example_placeholder` is used as `%libreforge_example_placeholder%`.

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the placeholder will not load.
:::

## The structure of a placeholder

| Part | What it controls |
| --- | --- |
| **Static value** | A fixed text or number |
| **Expression value** | A math expression resolved on use |
| **Conditional value** | A value chosen by conditions, with a default |

```yaml
placeholders:
  - id: "example_placeholder" # === Static value ===
    value: "This is an example placeholder!" # The value returned

  - id: "example_expression_placeholder" # === Expression value ===
    value: "%level% * 2" # Math expressions are fully supported

  - id: "conditional_placeholder" # === Conditional value ===
    default: 5 # Optional; the value when no conditions match
    values:
      - conditions: # Full condition system support
          - id: has_permission
            args:
              permission: "ecomc.rank.netherite"
        value: 20
      - conditions:
          - id: has_permission
            args:
              permission: "ecomc.rank.diamond"
        value: 10
```

### Static value

The simplest placeholder returns a fixed string or number.

```yaml
- id: "example_placeholder"
  value: "This is an example placeholder!" # Returned as-is
```

### Expression value

A `value` can be a math expression, resolved each time the placeholder is read.

```yaml
- id: "example_expression_placeholder"
  value: "%level% * 2" # Math expressions are fully supported
```

### Conditional value

Use `values` to return different results based on conditions, with an optional `default` for when none match. The first matching entry wins.

```yaml
- id: "conditional_placeholder"
  default: 5 # Optional; used when no conditions match
  values:
    - conditions:
        - id: has_permission
          args:
            permission: "ecomc.rank.netherite"
      value: 20
    - conditions:
        - id: has_permission
          args:
            permission: "ecomc.rank.diamond"
      value: 10
```

:::danger Conditions are their own system
The `conditions` used in a conditional placeholder are configured the same way everywhere in libreforge, so they are documented separately.

- [Configuring a Condition](configuring-a-condition)
:::

:::tip Troubleshooting
- **Placeholder showing as raw text?** Check it is referenced as `%libreforge_<id>%` and that the `id` is lowercase letters, numbers, and underscores only.
- **Expression not calculating?** Make sure every placeholder inside the `value` resolves to a number.
- **Conditional always returns the default?** Confirm at least one entry's conditions pass for the player; the first match wins.
:::

<hr/>

## Where to go next

- **Conditions:** [Configuring a Condition](configuring-a-condition) for conditional placeholder values.
- **Arguments:** [Custom Arguments](custom-arguments) to reuse effect logic, not just values.
- **Points:** [The Points System](points) for per-player data you can read in placeholders.
