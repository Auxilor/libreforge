package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.arguments
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
    override val arguments = arguments {
        require("fortune", "You must specify the level of fortune to mimic!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val event = data.event as? WrappedDropEvent<*> ?: return

        event.modifiers += {
            val fortune = config.getIntFromExpression("fortune", data)

            var matches = true
            if (config.has("on_items")) {
                val items = config.getStrings("on_items").map { string -> Items.lookup(string) }
                matches = items.any { test -> test.matches(it) }
            }

            if (fortune > 0 && it.maxStackSize > 1 && matches) {
                it.amount = (Math.random() * (fortune.toDouble() - 1) + 1.1).roundToInt()
            }

            Pair(it, 0)
        }
    }
}
