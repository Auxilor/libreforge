package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config

class ConfigArguments internal constructor(
    builder: ConfigArgumentsBuilder
) {
    private val arguments = builder.arguments

    fun test(config: Config): List<ConfigViolation> {
        return arguments.flatMap { it.test(config) }
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

    fun inherit(getter: (Config) -> Compilable<*>?) {
        arguments += InheritedArguments(getter)
    }

    fun inherit(subsection: String, getter: (Config) -> Compilable<*>?) {
        arguments += InheritedArguments(getter, subsection)
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
    fun test(config: Config): List<ConfigViolation>
}

private class RequiredArgument<T>(
    val names: Collection<String>,
    val message: String,
    val getter: Config.(String) -> T,
    val predicate: (T) -> Boolean
) : ConfigArgument {
    override fun test(config: Config): List<ConfigViolation> {
        for (key in names) {
            if (config.has(key) && predicate(config.getter(key))) {
                return emptyList()
            }
        }

        return listOf(ConfigViolation(names.first(), message))
    }
}

private class InheritedArguments(
    val getter: (Config) -> Compilable<*>?,
    val subsection: String? = null
) : ConfigArgument {
    override fun test(config: Config): List<ConfigViolation> {
        val section = if (subsection == null) config else config.getSubsection(subsection)

        return getter(config)?.arguments?.test(section) ?: emptyList()
    }
}
