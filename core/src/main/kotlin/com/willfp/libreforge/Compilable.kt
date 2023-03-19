package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registrable

/*
Sometimes you don't need any compile data,
so you use this as the type parameter instead.
 */

object NoCompileData

class InvalidCompileDataException(
    override val message: String
) : Exception(message)

interface Compiled<T> {
    val config: Config
    val compileData: T
}

abstract class Compilable<T> : Registrable {
    /**
     * The ID.
     */
    abstract val id: String

    /**
     * The arguments that will be checked.
     */
    open val arguments: ConfigArguments = arguments { }

    /**
     * @param config The config.
     * @param context The context to log violations for.
     * @return The compile data.
     */
    open fun makeCompileData(config: Config, context: ViolationContext): T {
        /*

        Dodgy 'solution' to things that don't have any compile data,
        which is to force them to use the NoCompileData type parameter.

        If there's no compile data, and you're not using NoCompileData as
        the type parameter, then you'll get an exception.

        It's done this way so compilable objects aren't littered with random
        makeCompileData overrides that return null.

         */

        @Suppress("UNCHECKED_CAST")
        return NoCompileData as? T
            ?: throw InvalidCompileDataException(
                "You must override makeCompileData or use NoCompileData as the type!"
            )
    }

    /**
     * Check if the config for this is valid.
     *
     * Will send messages to console if invalid.
     *
     * @param config The config.
     * @param context The context.
     * @return If the config is valid.
     */
    fun checkConfig(config: Config, context: ViolationContext): Boolean {
        val violations = arguments.test(config)

        for (violation in violations) {
            context.log(this, violation)
        }

        return violations.isEmpty()
    }

    override fun getID() = id
}
