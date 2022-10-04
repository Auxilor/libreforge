package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedDropEvent
import kotlin.math.roundToInt

class EffectMultiplyDrops : Effect(
    "multiply_drops",
    triggers = Triggers.withParameters(
        TriggerParameter.EVENT
    ),
    noDelay = true
) {
    override fun handle(data: TriggerData, config: Config) {
        val event = data.event as? WrappedDropEvent<*> ?: return

        event.modifiers += {
            val fortune = config.getIntFromExpression("fortune", data)

            val items = config.getStrings("on_items").map { string -> Items.lookup(string) }
            val matches = items.any { test -> test.matches(it) }

            if (fortune > 0 && it.maxStackSize > 1 && matches) {
                it.amount = (Math.random() * (fortune.toDouble() - 1) + 1.1).roundToInt()
            }

            Pair(it, 0)
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("fortune")) violations.add(
            ConfigViolation(
                "fortune",
                "You must specify the fortune to give!"
            )
        )

        if (!config.has("on_items")) violations.add(
            ConfigViolation(
                "on_items",
                "You must specify the items that can be multiplied!"
            )
        )

        return violations
    }
}