package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config

abstract class ConfigurableProperty {
    /**
     * The ID.
     */
    abstract val id: String

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
            context.log(this, violation)
        }

        return violations.isNotEmpty()
    }
}
