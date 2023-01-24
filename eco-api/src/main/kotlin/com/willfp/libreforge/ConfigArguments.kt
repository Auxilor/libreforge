package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config

class ConfigArguments internal constructor(
    builder: ConfigArgumentsBuilder
) {
    private val arguments = builder.arguments

    fun test(config: Config): List<ConfigViolation> {
        return arguments.mapNotNull { it.test(config) }
    }
}

class ConfigArgumentsBuilder {
    internal val arguments = mutableListOf<ConfigArgument>()

    fun require(name: String, message: String) {
        require(listOf(name), message)
    }

    fun <T> require(
        name: String,
        message: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean
    ) {
        require(listOf(name), message, getter, predicate)
    }

    fun require(names: Collection<String>, message: String) {
        @Suppress("UNCHECKED_CAST")
        require(names, message, { get(it) }) {
            true
        }
    }

    fun <T> require(
        names: Collection<String>,
        message: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean
    ) {
        arguments += RequiredArgument(names, message, getter, predicate)
    }
}

fun arguments(block: ConfigArgumentsBuilder.() -> Unit): ConfigArguments {
    val builder = ConfigArgumentsBuilder()
    block(builder)
    return ConfigArguments(builder)
}

interface ConfigArgument {
    /**
     * Null if valid.
     */
    fun test(config: Config): ConfigViolation?
}

private class RequiredArgument<T>(
    val names: Collection<String>, val message: String, val getter: Config.(String) -> T, val predicate: (T) -> Boolean
) : ConfigArgument {
    override fun test(config: Config): ConfigViolation? {
        for (key in names) {
            if (config.has(key) && predicate(config.getter(key))) {
                return null
            }
        }

        return ConfigViolation(names.first(), message)
    }
}
