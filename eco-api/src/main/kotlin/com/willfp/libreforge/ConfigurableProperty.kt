package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import java.util.Objects

abstract class ConfigurableProperty(
    val id: String
) {
    protected val plugin = LibReforge.plugin

    /**
     * Check if the config for this is valid.
     *
     * Will send messages to console if invalid.
     *
     * @param config The config.
     * @param context Additional context, e.g. path in config.
     * @return If any violations.
     */
    fun checkConfig(config: Config, context: String): Boolean {
        val violations = this.validateConfig(config)

        for (violation in violations) {
            LibReforge.logViolation(this.id, context, violation)
        }

        return violations.isNotEmpty()
    }

    /**
     * Check if the config for this effect is valid.
     *
     * @param config The config.
     * @return A list of violations.
     */
    protected open fun validateConfig(config: Config): List<ConfigViolation> {
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
