---
title: "Bedrock Forms"
sidebar_position: 13
---

Bedrock forms are the native menus that Minecraft: Bedrock Edition shows on screen: a list of buttons, a yes/no dialog, or a page of inputs and sliders. libreforge can send all three to Bedrock players who join through Geyser, and react to whatever they pick.

This is the right tool when a chest GUI would be awkward. Bedrock players get a translated chest inventory that has no hover tooltips, no shift click parity, and a cramped slot layout on Pocket UI. A form is a real Bedrock interface, so it looks and behaves the way those players expect, and it is the only way to ask them to type something.

## Requirements

Everything on this page requires the [Floodgate](https://geysermc.org/wiki/floodgate/) plugin, which is what tells the server that a player arrived from Bedrock. Without it, none of these effects, triggers, or filters are registered at all, and configs referencing them will log an unknown ID.

Forms only work for Bedrock players. Sending one to a Java player does nothing and the effect returns false, so it will not break a chain, it simply will not show anything. Guard on it with the `is_bedrock_player` condition when you want a different path for Java players.

## The three form types

| Effect | Bedrock form | Use it for |
| --- | --- | --- |
| `send_bedrock_form` | SimpleForm | A menu: any number of buttons, optionally with images |
| `send_bedrock_modal_form` | ModalForm | A confirmation: exactly two buttons, no more, no fewer |
| `send_bedrock_custom_form` | CustomForm | A questionnaire: text inputs, toggles, sliders, dropdowns |

## How a form talks back

Every form has two possible endings, and libreforge gives you both.

1. The player answers it. Any commands you configured for their choice run first, then the `bedrock_form_response` trigger fires.
2. The player dismisses it. Nothing runs, and the `bedrock_form_closed` trigger fires instead.

Commands are the quick path, good for a menu that just opens other menus. Triggers are the full path, letting you hang any libreforge effect chain off a button press. You can use both at once on the same form.

Both triggers carry the player, their location, and the event. `bedrock_form_response` also carries:

| Parameter | What it holds |
| --- | --- |
| `text` | The clicked button's text, or the first answer on a custom form |
| `value` | The clicked button's index, counting from 0, or -1 on a custom form |

Because both triggers fire for every form your server sends, always filter on `bedrock_form_id` so a chain only reacts to its own form. Without that filter, opening any form anywhere will run your chain.

## A menu with buttons

`send_bedrock_form` shows a vertical list of buttons. Each button can run its own commands, and can carry an image.

```yaml
effects:
  - id: send_bedrock_form
    triggers:
      - right_click
    args:
      title: "&aServer Menu"
      content: "Pick where you want to go" # Optional
      form_id: server_menu # Used later by the bedrock_form_id filter
      buttons:
        - text: "Spawn"
          commands:
            - "spawn %player%"
        - text: "Shop"
          image_type: PATH
          image: "textures/items/emerald"
          commands:
            - "shop open %player%"
        - text: "Warps"
          image_type: URL
          image: "https://example.com/warps.png"
```

`image_type` is either `PATH` for a texture inside the Bedrock client or resource pack, or `URL` for an image fetched over the network. Both `image_type` and `image` must be set for an image to appear; leave both out for a plain button.

The third button has no commands. It is still perfectly usable: react to it with the trigger instead.

## A confirmation dialog

`send_bedrock_modal_form` is a two button dialog. Bedrock does not allow any other number of buttons on this form type.

```yaml
effects:
  - id: send_bedrock_modal_form
    triggers:
      - right_click
    args:
      title: "&cConfirm"
      content: "Sell your entire inventory for $500?"
      form_id: confirm_sell
      button1: "Yes"
      button2: "No"
      button1_commands:
        - "sellall %player%"
        - "eco give %player% 500"
      button2_commands:
        - "tell %player% Cancelled."
```

On the response trigger, `value` is 0 for the first button and 1 for the second.

Closing the dialog is not the same as pressing the second button. If you want a dismissal to count as "no", handle `bedrock_form_closed` as well.

## A form with inputs

`send_bedrock_custom_form` is the only way to get typed text out of a Bedrock player. It builds a page from a list of components, in the order you write them.

```yaml
effects:
  - id: send_bedrock_custom_form
    triggers:
      - right_click
    args:
      title: "&aApply for staff"
      form_id: staff_application
      components:
        - type: label
          text: "Tell us about yourself."
        - type: input
          text: "Your age"
          placeholder: "18"
        - type: dropdown
          text: "Preferred role"
          options:
            - "Helper"
            - "Moderator"
            - "Builder"
        - type: slider
          text: "Hours per week"
          min: 1
          max: 40
          step: 1
          default: 10
        - type: toggle
          text: "I have read the rules"
      commands:
        - "staffapp submit %player% %answer_1% %answer_2% %answer_3% %answer_4%"
```

### Components

| `type` | Shows | Extra keys |
| --- | --- | --- |
| `label` | Static text, no input | none |
| `input` | A single line text box | `placeholder`, `default` |
| `toggle` | An on/off switch | `default` (true or false) |
| `slider` | A numeric slider | `min`, `max`, `step`, `default` |
| `dropdown` | A drop down list | `options`, `default` (option index) |
| `step_slider` | A slider that snaps between named options | `options`, `default` (option index) |

Every extra key is optional, but a slider with no `min` and `max` runs from 0 to 0 and cannot be moved, so always set both. `step` defaults to 1, and `default` falls back to `min` on a slider and to the first option elsewhere.

### Reading the answers

Answers are numbered from 1 in config order, and **labels are skipped**, because a label has nothing to answer. In the example above `%answer_1%` is the age input, `%answer_2%` is the role, `%answer_3%` is the hours slider, and `%answer_4%` is the toggle, even though the label sits at the top.

Values arrive as text:

- An input gives exactly what was typed, which may be an empty string.
- A toggle gives `true` or `false`.
- A slider gives a number, without a trailing `.0` when it is whole.
- A dropdown and a step slider give the **option text**, not its index, so `Moderator` rather than `1`.

`%answer_n%` placeholders work in the `commands` list only. On the `bedrock_form_response` trigger, `text` is set to the first answer.

## Reacting with a chain

To run real libreforge effects instead of commands, listen for the response trigger and filter on the form ID.

```yaml
effects:
  # Send the menu
  - id: send_bedrock_form
    triggers:
      - right_click
    args:
      title: "&aRewards"
      form_id: rewards_menu
      buttons:
        - text: "Daily reward"
        - text: "Weekly reward"

  # React to it
  - id: give_money
    triggers:
      - bedrock_form_response
    filters:
      bedrock_form_id: rewards_menu
      value_equals: 0 # The first button
    args:
      amount: 500

  - id: send_message
    triggers:
      - bedrock_form_closed
    filters:
      bedrock_form_id: rewards_menu
    args:
      message: "&7Menu closed."
```

Filter buttons by position with `value_equals`, or by label with `text`. Position is the sturdier of the two, since it survives renaming a button or translating it.

## Closing a form early

`close_bedrock_form` dismisses whatever form the player currently has open. This fires `bedrock_form_closed`, the same as if they had dismissed it themselves.

```yaml
effects:
  - id: close_bedrock_form
    triggers:
      - take_damage
```

## Things worth knowing

**A player can always walk away.** Closing a form is not preventable, so never write a chain that assumes a button will eventually be pressed. If something must happen either way, put it before the form is sent, or handle `bedrock_form_closed` too.

**One form at a time.** Bedrock shows a single form on screen, so sending a second one while another is open is not something to rely on. To chain menus together, send the next form from the response trigger of the previous one rather than sending both at once.

**Placeholders are parsed twice.** Titles, content, and button labels are parsed when the form is sent. Commands are parsed when the player answers, against the trigger data from when it was sent. A form left open for five minutes will still use the state it was opened with.

**Responses arrive off the main thread.** libreforge already moves everything back onto the main thread before running commands or firing triggers, so this only matters if you are handling `BedrockFormEvent` in your own plugin.

**Form IDs are yours to pick.** They are matched case insensitively and are not registered anywhere, so any string works. Leaving `form_id` unset gives an empty ID, which is fine for a form nothing reacts to, but two such forms cannot be told apart.

## For plugin developers

Both triggers are backed by `BedrockFormEvent`, a normal Bukkit event you can listen for directly. It carries the form ID, the form type, the player, whether it was closed, the clicked button's index and text, and the full list of custom form answers, which is the only way to read answers past the first one in code.

```java
@EventHandler
public void onForm(BedrockFormEvent event) {
    if (!event.getFormId().equals("staff_application") || event.isClosed()) {
        return;
    }

    List<String> answers = event.getInputs();
    // ...
}
```

## Related

Beyond forms, the Floodgate integration adds conditions and filters for telling Bedrock players apart: `is_bedrock_player`, `bedrock_device_os`, `bedrock_input_mode`, `bedrock_ui_profile`, `bedrock_version`, and `has_linked_java_account`. `bedrock_input_mode` in particular pairs well with forms, since touch players benefit most from a native interface.
