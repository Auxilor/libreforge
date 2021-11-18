package com.willfp.libreforge.api

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.effects.ConfigViolation
import com.willfp.libreforge.api.effects.Effect
import java.util.Objects

abstract class ConfigurableProperty(
    val id: String
) {
    protected val plugin = LibReforge.instance.plugin

    /**
     * Check if the config for this effect is valid.
     *
     * Will send messages to console if invalid.
     *
     * @param config The config.
     * @param context Additional context, e.g. path in config.
     */
    fun checkConfig(config: JSONConfig, context: String) {
        val violations = validateConfig(config)

        for (violation in violations) {
            plugin.logger.warning("Invalid configuration for $id in context $context:")
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
    protected open fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        return emptyList()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Effect) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}