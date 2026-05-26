---
title: "Custom Placeholders"
sidebar_position: 10
---

You can create custom placeholders to reuse mathematical expressions or to have global
data shared between plugins.

You can configure these in /plugins/libreforge/placeholders.yml, and look like this:

```yaml
placeholders:
    - id: "example_placeholder" # The placeholder ID
      value: "This is an example placeholder!" # The value of the placeholder

    - id: "example_expression_placeholder"
      value: "%level% * 2" # Mathematical expressions are fully supported!

    - id: "conditional_placeholder"
      default: 5 # (Optional) Specify a default value if no conditions are true
      values:
        - conditions: # Full condition system support!
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

You can create as many placeholders as you want by adding to the list.

Placeholders can be referenced with `%libreforge_<id>%`, and are fully supported with PlaceholderAPI.