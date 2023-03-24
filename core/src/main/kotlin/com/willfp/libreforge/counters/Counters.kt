package com.willfp.libreforge.counters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.separatorAmbivalent
import com.willfp.libreforge.triggers.Triggers

@Suppress("UNUSED")
object Counters {
    /**
     * Compile a counter from a [cfg] in a [context].
     */
    @JvmStatic
    fun compile(
        cfg: Config,
        context: ViolationContext
    ): Counter? {
        val config = cfg.separatorAmbivalent()

        // Legacy support for 'id' instead of 'trigger'
        val id = if (config.has("trigger")) config.getString("trigger") else config.getString("id")

        val trigger = Triggers[id]

        if (trigger == null) {
            context.log(
                ConfigViolation(
                    "trigger",
                    "Invalid trigger ID specified!"
                )
            )

            return null
        }

        val multiplier = if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier")
        } else {
            1.0
        }

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("conditions")
        )

        val filters = Filters.compile(
            config.getSubsection("filters"),
            context.with("filters")
        )

        return Counter(
            trigger,
            multiplier,
            conditions,
            filters
        )
    }
}
