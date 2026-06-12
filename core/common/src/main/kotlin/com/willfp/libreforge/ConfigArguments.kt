package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import kotlin.reflect.KClass

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

    // Config-section lists (consumed by the config builder as nested element sockets)
    EFFECT_LIST,      // value is a list of effect config sections
    CONDITION_LIST,   // value is a list of condition config sections

    // List of structured subsections described by a schema DTO (see ArgumentMeta.Regular.schema)
    DYNAMIC,

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
        val choices: List<String>,
        /**
         * For [ArgType.DYNAMIC] args: a documentation-only DTO describing the keys of each
         * subsection in the list. Non-null properties are required keys, nullable properties
         * are optional keys; KDoc `@property` lines supply per-key descriptions. A List<String>
         * property with a listOf(...) default is a fixed set of choices (rendered as a dropdown);
         * without a default it is a free list of user entries. The wiki parser reads this class
         * from source — it is never instantiated at runtime.
         */
        val schema: KClass<*>? = null
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

    @JvmOverloads
    fun require(
        name: String,
        message: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList(),
        schema: KClass<*>? = null
    ) {
        require(listOf(name), message, description, type, choices, schema)
    }

    fun <T> require(
        name: String,
        message: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean
    ) {
        require(listOf(name), message, getter, predicate)
    }

    @JvmOverloads
    fun require(
        names: Collection<String>,
        message: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList(),
        schema: KClass<*>? = null
    ) {
        require(names, message, { get(it) }, { true }, description, type, choices, schema)
    }

    @JvmOverloads
    fun <T> require(
        names: Collection<String>,
        message: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList(),
        schema: KClass<*>? = null
    ) {
        arguments += RequiredArgument(names, message, getter, predicate, description, type, choices, schema)
    }

    @JvmOverloads
    fun optional(
        name: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        default: String? = null,
        choices: List<String> = emptyList(),
        schema: KClass<*>? = null
    ) {
        arguments += OptionalArgument(listOf(name), description, type, default, choices, schema)
    }

    @JvmOverloads
    fun optional(
        names: Collection<String>,
        description: String = "",
        type: ArgType = ArgType.ANY,
        default: String? = null,
        choices: List<String> = emptyList(),
        schema: KClass<*>? = null
    ) {
        arguments += OptionalArgument(names, description, type, default, choices, schema)
    }

    /**
     * Attach wiki metadata to a previously registered [require] that used a getter/predicate.
     * Call immediately after the require it describes.
     */
    @JvmOverloads
    fun describe(
        name: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        choices: List<String> = emptyList()
    ) {
        val arg = arguments
            .filterIsInstance<RequiredArgument<*>>()
            .lastOrNull { name in it.argNames }
            ?: return

        arg.meta = arg.meta.copy(
            description = description,
            type = type,
            choices = choices
        )
    }

    fun inherit(
        getter: (Config) -> Compilable<*>?
    ) {
        arguments += InheritedArguments(getter, null, "")
    }

    fun inherit(
        subsection: String,
        getter: (Config) -> Compilable<*>?
    ) {
        arguments += InheritedArguments(getter, subsection, "")
    }

    /**
     * Attach a description to the root-level [inherit] (no subsection).
     */
    fun describeInherit(description: String) {
        val arg = arguments.filterIsInstance<InheritedArguments>().firstOrNull { it.subsection == null } ?: return
        arg.meta = arg.meta.copy(description = description)
    }

    /**
     * Attach a description to the [inherit] registered with the given [subsection].
     */
    fun describeInherit(subsection: String, description: String) {
        val arg = arguments.filterIsInstance<InheritedArguments>().firstOrNull { it.subsection == subsection } ?: return
        arg.meta = arg.meta.copy(description = description)
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
    val argNames: Collection<String>,
    private val message: String,
    private val getter: Config.(String) -> T,
    private val predicate: (T) -> Boolean,
    description: String,
    type: ArgType,
    choices: List<String>,
    schema: KClass<*>? = null
) : ConfigArgument {
    override var meta: ArgumentMeta.Regular = ArgumentMeta.Regular(
        names = argNames,
        description = description,
        type = type,
        required = true,
        default = null,
        choices = choices,
        schema = schema
    )

    override fun test(config: Config): List<ConfigViolation> {
        for (name in argNames) {
            val value = config.getter(name)
            if (config.has(name) && predicate(value)) {
                return emptyList()
            }
        }

        return listOf(ConfigViolation(argNames.first(), message))
    }
}

private class OptionalArgument(
    private val names: Collection<String>,
    private val description: String,
    private val type: ArgType,
    private val default: String?,
    private val choices: List<String>,
    schema: KClass<*>? = null
) : ConfigArgument {
    override val meta = ArgumentMeta.Regular(
        names = names,
        description = description,
        type = type,
        required = false,
        default = default,
        choices = choices,
        schema = schema
    )

    override fun test(config: Config): List<ConfigViolation> = emptyList()
}

private class InheritedArguments(
    private val getter: (Config) -> Compilable<*>?,
    val subsection: String? = null,
    description: String = ""
) : ConfigArgument {
    override var meta: ArgumentMeta.Inherited = ArgumentMeta.Inherited(
        subsection = subsection,
        description = description
    )

    override fun test(config: Config): List<ConfigViolation> {
        val section = subsection?.let { config.getSubsection(it) } ?: config
        val compilable = getter(section)

        return compilable?.arguments?.test(section) ?: emptyList()
    }
}
