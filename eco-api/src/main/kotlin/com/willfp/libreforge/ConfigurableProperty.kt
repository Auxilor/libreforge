package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import java.util.Objects

abstract class ConfigurableProperty(
    val id: String
) {
    protected val plugin = LibReforgePlugin.instance

    /**
     * The arguments that will be checked.
     */
    open val arguments: ConfigArguments = arguments { }

    /**
     * Check if the config for this is valid.
     *
     * Will send messages to console if invalid.
     *
     * @param config The config.
     * @param context The context.
     * @return If any violations are found, take true to be a failure.
     */
    fun checkConfig(config: Config, context: ViolationContext): Boolean {
        val violations = arguments.test(config)

        for (violation in violations) {
            plugin.logViolation(this.id, context, violation)
        }

        return violations.isNotEmpty()
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
