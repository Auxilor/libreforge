package com.willfp.libreforge.triggers

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.separatorAmbivalent

@Suppress("UNUSED")
object Counters {
    /**
     * Compile a counter.
     *
     * @param cfg The config for the effect chain.
     * @param context The context to log violations for.
     * @return The counter, or null if invalid.
     */
    @JvmStatic
    fun compile(cfg: Config, context: String): Counter? {
        val config = cfg.separatorAmbivalent()

        val id = config.getString("trigger")

        val trigger = Triggers.getById(id)

        if (trigger == null) {
            LibReforgePlugin.instance.logViolation(
                id,
                context,
                ConfigViolation("trigger", "Invalid trigger ID specified!")
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
            "$context -> Conditions"
        )

        val filters = config.getSubsection("filters")

        return Counter(
            trigger,
            multiplier,
            conditions,
            filters
        )
    }
}
