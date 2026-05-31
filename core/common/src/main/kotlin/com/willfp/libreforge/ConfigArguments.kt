package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config

/**
 * The data type of a config argument, used for wiki documentation and config builders.
 */
enum class ArgType {
    // Primitives
    BOOLEAN,
    INT,
    DOUBLE,
    STRING,
    EXPRESSION,       // Mathematical expression supporting placeholders

    // Minecraft types
    BLOCK,
    ITEM,
    ENTITY,
    SOUND,
    POTION_EFFECT,
    ENCHANTMENT,
    MATERIAL,
    COLOR,

    // List variants
    BOOLEAN_LIST,
    INT_LIST,
    DOUBLE_LIST,
    STRING_LIST,
    BLOCK_LIST,
    ITEM_LIST,
    ENTITY_LIST,
    POTION_EFFECT_LIST,
    ENCHANTMENT_LIST,

    // Fallback
    ANY
}

/**
 * Documentation metadata for a single config argument or inherited sub-section.
 * Consumed by the wiki parser to build config documentation and interactive builders.
 */
sealed class ArgumentMeta {
    /**
     * A concrete argument with a known name, type, and requirements.
     */
    data class Regular(
        /** The config key(s) — multiple means any one satisfies the requirement. */
        val names: Collection<String>,
        /** Human-written description of what this argument does in context. */
        val description: String,
        /** The expected data type. */
        val type: ArgType,
        /** Whether this argument must be present. */
        val required: Boolean,
        /** Default value as a string (for optional args), e.g. "false", "1.0", "[]". */
        val default: String?,
        /** Fixed set of accepted values for enum-like arguments. */
        val choices: List<String>
    ) : ArgumentMeta()

    /**
     * A dynamically-inherited sub-section whose arguments depend on a runtime value.
     * The wiki should link to the referenced sub-compilable's own documentation.
     */
    data class Inherited(
        /** The config sub-section key, or null if arguments are at the root level. */
        val subsection: String?,
        /** Human-written description of what this sub-section configures. */
        val description: String
    ) : ArgumentMeta()
}

class ConfigArguments internal constructor(
    private val arguments: List<ConfigArgument>
) {
    /** All argument metadata, for use by the wiki parser and config builders. */
    val docs: List<ArgumentMeta> get() = arguments.map { it.meta }

    fun test(config: Config): List<ConfigViolation> {
        return arguments.flatMap { it.test(config) }
    }
}

class ConfigArgumentsBuilder {
    private val arguments = mutableListOf<ConfigArgument>()

    fun require(
        name: String,
        message: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList()
    ) {
        require(listOf(name), message, description, type, choices)
    }

    fun <T> require(
        name: String,
        message: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList()
    ) {
        require(listOf(name), message, getter, predicate, description, type, choices)
    }

    fun require(
        names: Collection<String>,
        message: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList()
    ) {
        require(names, message, { get(it) }, { true }, description, type, choices)
    }

    fun <T> require(
        names: Collection<String>,
        message: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList()
    ) {
        arguments += RequiredArgument(names, message, getter, predicate, description, type, choices)
    }

    fun optional(
        name: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        default: String? = null,
        choices: List<String> = emptyList()
    ) {
        arguments += OptionalArgument(listOf(name), description, type, default, choices)
    }

    fun optional(
        names: Collection<String>,
        description: String = "",
        type: ArgType = ArgType.ANY,
        default: String? = null,
        choices: List<String> = emptyList()
    ) {
        arguments += OptionalArgument(names, description, type, default, choices)
    }

    fun inherit(
        getter: (Config) -> Compilable<*>?,
        description: String = ""
    ) {
        arguments += InheritedArguments(getter, null, description)
    }

    fun inherit(
        subsection: String,
        getter: (Config) -> Compilable<*>?,
        description: String = ""
    ) {
        arguments += InheritedArguments(getter, subsection, description)
    }

    internal fun build() = ConfigArguments(arguments)
}

fun arguments(block: ConfigArgumentsBuilder.() -> Unit): ConfigArguments {
    return ConfigArgumentsBuilder().apply(block).build()
}

interface ConfigArgument {
    val meta: ArgumentMeta

    fun test(config: Config): List<ConfigViolation>
}

private class RequiredArgument<T>(
    private val names: Collection<String>,
    private val message: String,
    private val getter: Config.(String) -> T,
    private val predicate: (T) -> Boolean,
    private val description: String,
    private val type: ArgType,
    private val choices: List<String>
) : ConfigArgument {
    override val meta = ArgumentMeta.Regular(
        names = names,
        description = description,
        type = type,
        required = true,
        default = null,
        choices = choices
    )

    override fun test(config: Config): List<ConfigViolation> {
        for (name in names) {
            val value = config.getter(name)
            if (config.has(name) && predicate(value)) {
                return emptyList()
            }
        }

        return listOf(ConfigViolation(names.first(), message))
    }
}

private class OptionalArgument(
    private val names: Collection<String>,
    private val description: String,
    private val type: ArgType,
    private val default: String?,
    private val choices: List<String>
) : ConfigArgument {
    override val meta = ArgumentMeta.Regular(
        names = names,
        description = description,
        type = type,
        required = false,
        default = default,
        choices = choices
    )

    override fun test(config: Config): List<ConfigViolation> = emptyList()
}

private class InheritedArguments(
    private val getter: (Config) -> Compilable<*>?,
    private val subsection: String? = null,
    private val description: String = ""
) : ConfigArgument {
    override val meta = ArgumentMeta.Inherited(
        subsection = subsection,
        description = description
    )

    override fun test(config: Config): List<ConfigViolation> {
        val section = subsection?.let { config.getSubsection(it) } ?: config
        val compilable = getter(section)

        return compilable?.arguments?.test(section) ?: emptyList()
    }
}
