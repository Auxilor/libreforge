package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin

/**
 * An invalid configuration will flag up a violation.
 */
data class ConfigViolation(val param: String, val message: String)

/**
 * A context in which a violation occurred.
 */
class ViolationContext {
    private val plugin: EcoPlugin

    private val parents: List<String>

    constructor(plugin: EcoPlugin) {
        this.plugin = plugin
        parents = listOf()
    }

    constructor(plugin: EcoPlugin, context: String) {
        this.plugin = plugin
        parents = listOf(context)
    }

    internal constructor(plugin: EcoPlugin, parents: List<String>) {
        this.plugin = plugin
        this.parents = parents
    }

    /**
     * Copy the violation context with an extra added context.
     */
    fun with(context: String) = ViolationContext(plugin, parents + context)

    /**
     * Log a violation.
     */
    fun log(property: ConfigurableProperty, violation: ConfigViolation) {
        plugin.logger.warning("")
        plugin.logger.warning("Invalid configuration for ${property.id} found at $this:")
        plugin.logger.warning("(Cause) Argument '${violation.param}'")
        plugin.logger.warning("(Reason) ${violation.message}")
        plugin.logger.warning("")
    }

    override fun toString(): String {
        return parents.joinToString(" -> ")
    }
}
