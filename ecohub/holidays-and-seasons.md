---
title: "Holidays and Seasons"
sidebar_position: 13
---

Holidays and seasons let you run effects on **real-world dates**, like double XP on Christmas Day or a summer shop. You define each one in a config file, and libreforge gives it a **condition**, a **filter**, and **start and end triggers** you can use anywhere. This page covers creating one, naming it, the date options, and the components each one gets.

## Quick start

1. Open the `holidays.yml` or `seasons.yml` file in `/plugins/libreforge`.
2. Add an entry under `holidays:` or `seasons:` with an `id` and a `date`.
3. Run `/libreforge reload`.
4. Use the `is_<id>` condition on an effect, and confirm it only runs on your date.

:::tip
Holidays and seasons work exactly the same way. The only difference is which file they live in, so use whichever name fits.
:::

## Naming and IDs

Each holiday or season needs an `id`. It names the condition, filter, and triggers it gets, so a holiday with `id: christmas_day` gives you `is_christmas_day`, `christmas_day_start`, and `christmas_day_end`.

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the holiday will not load. The ID `easter` is reserved for the built-in `%easter%` date, and an ID that clashes with an existing condition, filter, or trigger is skipped.
:::

## The structure of a holiday

| Part | What it controls |
| --- | --- |
| **Dates** | The days of the year it is on |
| **Calendar options** | The month, day of the week, or week of the month it must fall in |
| **Offset** | Moving it earlier or later by a number of days |

Every option you set must match for the holiday to be on. A list matches if any value in it does.

```yaml
holidays:
  - id: christmas_day # === Dates ===
    date: "12-25" # The same day every year

  - id: thanksgiving # === Calendar options ===
    month: november # Month names or numbers
    day_of_week: thursday # Day names or numbers (1 = Monday ... 7 = Sunday)
    week: 4 # The fourth Thursday of the month

  - id: black_friday # === Offset ===
    date: "%thanksgiving%" # Whenever the thanksgiving holiday is on
    date_offset: 1 # One day later
```

### Dates

Set `date` for one date, or `dates` for a list. Always quote dates.

| Format | Meaning |
| --- | --- |
| `"12-25"` | The same day every year |
| `"2026-11-08"` | One day only |
| `"12-25..01-05"` | Every day in between, and can cross New Year |
| `"2027-02-06..2027-02-20"` | Every day in between, once |
| `"%easter%"` | Easter Sunday, worked out for every year |
| `"%<id>%"` | Whenever another holiday in the same file is on |
| `"%<id>%..%<id>%"` | From the start of one holiday to the end of another |

```yaml
- id: diwali
  dates: # Dates that can't be calculated, like those on lunar calendars
    - "2026-11-08"
    - "2027-10-29"

- id: advent
  date: "%advent_sunday%..%christmas_eve%" # From one holiday to another
```

### Calendar options

| Option | Values |
| --- | --- |
| `month` / `months` | Month names (`november`) or numbers (1-12) |
| `day_of_week` / `days_of_week` | Day names (`thursday`) or numbers (1 = Monday ... 7 = Sunday) |
| `week` / `weeks` | Which one of its day of the week it is in the month (1-5, or `last`) |

```yaml
- id: memorial_day
  month: may
  day_of_week: monday
  week: last # The last Monday in May

- id: advent_sunday
  date: "11-27..12-03" # Options combine with dates too
  day_of_week: sunday
```

### Offset

`date_offset` moves a holiday a number of days later, or earlier if negative. Use a range to cover every day in between.

```yaml
- id: good_friday
  date: "%easter%"
  date_offset: -2 # Two days before Easter Sunday

- id: twelve_days_of_christmas
  date: "%christmas_day%"
  date_offset: 0..11 # Christmas Day and the 11 days after it
```

## Seasons

Seasons use the same options, in `seasons.yml`. The defaults are the northern hemisphere's meteorological seasons, and southern hemisphere servers can swap the months around.

```yaml
seasons:
  - id: summer
    months: [ june, july, august ]
  - id: winter
    months: [ december, january, february ]
```

## Using holidays and seasons

Every holiday and season gets these, which work like any other condition, filter, or trigger.

| Component | ID | What it does |
| --- | --- | --- |
| Condition | `is_<id>` | Passes while it is on |
| Filter | `is_<id>` | Matches while it is on (`true`) or off (`false`) |
| Trigger | `<id>_start` | Fires for every online player at midnight on its first day |
| Trigger | `<id>_end` | Fires for every online player at 23:59 on its last day |

```yaml
effects:
  - id: xp_multiplier
    args:
      multiplier: 2
    conditions:
      - id: is_christmas_day
```

:::danger Conditions, filters and triggers are their own system
These are configured the same way everywhere in libreforge, so they are documented separately.

- [Configuring an Effect](configuring-an-effect)
- [Configuring a Condition](configuring-a-condition)
:::

:::info Timezone
Dates are worked out in the timezone set by `dates.timezone` in `config.yml`, e.g. `"Europe/London"`. Leave it blank to use the server's timezone.
:::

:::tip Troubleshooting
- **Holiday not loading?** Check the console for a warning naming it; every holiday needs at least one of `date`, `month`, `day_of_week`, or `week`.
- **On the wrong day?** Check `dates.timezone` in `config.yml`, and that dates are quoted.
- **`%<id>%` not working?** It can only refer to another holiday in the same file, not a season.
- **Start trigger missed a player?** It only fires for players online at midnight; use the `is_<id>` condition for players who join later.
:::

<hr/>

## Where to go next

- **Conditions:** [Configuring a Condition](configuring-a-condition) to run effects only on a holiday.
- **Effects:** [Configuring an Effect](configuring-an-effect) to build what happens on the day.
- **Placeholders:** [Custom Placeholders](custom-placeholders) to show different values in a season.
