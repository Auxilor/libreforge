package com.willfp.libreforge.api.filter

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.LibReforge
import com.willfp.libreforge.api.effects.ConfigViolation

abstract class Filter {
    protected val plugin = LibReforge.plugin

    /**
     * Check if the config for this is valid.
     *
     * Will send messages to console if invalid.
     *
     * @param config The config.
     * @param context Additional context, e.g. path in config.
     */
    fun checkConfig(config: JSONConfig, context: String) {
        val violations = validateConfig(config)

        for (violation in violations) {
            plugin.logger.warning("Invalid configuration for filter in context $context:")
            plugin.logger.warning("(Cause) Missing argument ${violation.param}")
            plugin.logger.warning("(Fix) ${violation.message}")
        }
    }

    /**
     * Check if the config for this effect is valid.
     *
     * @param config The config.
     * @return A list of violations.
     */
    abstract fun validateConfig(config: JSONConfig): List<ConfigViolation>

    /**
     * Filter the object
     *
     * @param obj The object.
     * @return If matches filter.
     */
    abstract fun matches(obj: Any): Boolean
}