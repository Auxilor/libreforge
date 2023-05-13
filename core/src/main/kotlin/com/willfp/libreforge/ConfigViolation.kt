package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin

/**
 * An invalid configuration will flag up a violation.
 */
data class ConfigViolation(val param: String, val message: String)

/**
 * A logger for violations.
 */
interface ViolationLogger {
    /**
     * Log a violation.
     */
    fun log(context: ViolationContext, property: Compilable<*>, violation: ConfigViolation)

    /**
     * Log a violation.
     */
    fun log(context: ViolationContext, violation: ConfigViolation)
}

/**
 * A violation logger that logs to the plugin logger.
 */
class PluginViolationLogger(
    private val plugin: EcoPlugin
) : ViolationLogger {
    override fun log(context: ViolationContext, property: Compilable<*>, violation: ConfigViolation) {
        plugin.logger.warning("")
        plugin.logger.warning("Invalid configuration for ${property.id} found at $context:")
        plugin.logger.warning("(Cause) Argument '${violation.param}'")
        plugin.logger.warning("(Reason) ${violation.message}")
        plugin.logger.warning("")
    }

    override fun log(context: ViolationContext, violation: ConfigViolation) {
        plugin.logger.warning("")
        plugin.logger.warning("Invalid configuration found at $context:")
        plugin.logger.warning("(Cause) Argument '${violation.param}'")
        plugin.logger.warning("(Reason) ${violation.message}")
        plugin.logger.warning("")
    }
}


/**
 * A context in which a violation occurred.
 */
open class ViolationContext internal constructor(
    private val plugin: EcoPlugin,
    private val parents: List<String>,
    protected open val logger: ViolationLogger = PluginViolationLogger(plugin)
) {
    constructor(plugin: EcoPlugin) : this(plugin, emptyList())
    constructor(plugin: EcoPlugin, context: String) : this(plugin, listOf(context))

    /**
     * Copy the violation context with an extra added context.
     */
    fun with(context: String) = ViolationContext(plugin, parents + context, logger)

    /**
     * Log a violation.
     */
    fun log(property: Compilable<*>, violation: ConfigViolation) {
        logger.log(this, property, violation)
    }

    /**
     * Log a violation.
     */
    fun log(violation: ConfigViolation) {
        logger.log(this, violation)
    }

    override fun toString(): String {
        return parents.joinToString(" -> ")
    }
}

/**
 * A violation logger that does not log.
 */
object SilentViolationLogger : ViolationLogger {
    override fun log(context: ViolationContext, property: Compilable<*>, violation: ConfigViolation) {
        // Do nothing
    }

    override fun log(context: ViolationContext, violation: ConfigViolation) {
        // Do nothing
    }
}

/**
 * A violation context that does not log.
 */
object SilentViolationContext : ViolationContext(
    plugin,
    emptyList(),
    SilentViolationLogger
)
