package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropResult
import com.willfp.libreforge.triggers.event.EditableDropEvent
import kotlin.math.roundToInt

object EffectMultiplyDrops : Effect<NoCompileData>("multiply_drops") {
    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(listOf("multiplier", "fortune"), "You must specify a multiplier or level of fortune to mimic!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EditableDropEvent ?: return false

        val multiplier = if (config.has("fortune")) {
            val fortune = config.getIntFromExpression("fortune", data)
            (Math.random() * (fortune.toDouble() - 1) + 1.1).roundToInt()
        } else if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier", data).roundToInt()
        } else 1

        event.addModifier {
            var matches = true
            if (config.has("on_items")) {
                val items = config.getStrings("on_items").map { string -> Items.lookup(string) }
                matches = items.any { test -> test.matches(it) }
            }

            if (it.maxStackSize > 1 && matches) {
                it.amount *= multiplier
            }

            DropResult(it, 0)
        }

        return true
    }
}
