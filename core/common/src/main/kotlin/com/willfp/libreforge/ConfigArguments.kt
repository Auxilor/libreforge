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

    /*

    Minecraft types.

    These carry eco-specific lookup semantics — resource pack sounds, custom items,
    custom entities, and so on — so the accepted values are wider than the constants of any
    one Bukkit enum. Reach for them whenever the argument is resolved through an eco lookup
    (Items.lookup, Entities.lookup, PlayableSound, Particles.lookup, ...).

    For an argument that really is a bare Bukkit enum constant name, use STRING (or
    STRING_LIST / MAP) together with ArgumentMeta.Regular.enumClass instead, so the wiki
    links to the enum's JavaDoc rather than misrepresenting the lookup semantics.

     */

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

    // Arbitrary user-chosen keys mapping to a known value type (see mapKeyType / mapValueType)
    MAP,

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
        val schema: KClass<*>? = null,
        /**
         * Documentation-only example value for this argument. Either a `String`
         * scalar (e.g. `"1.5"`, `"%player_y% / 10"`) or a `listOf(...)` of strings
         * for list types. Never read at runtime; the wiki parser reads it from
         * source to build usage examples.
         */
        val example: Any? = null,
        /**
         * For [ArgType.MAP] args: the data type of the user-chosen keys. Documentation-only.
         */
        val mapKeyType: ArgType? = null,
        /**
         * For [ArgType.MAP] args: the data type of the values. Documentation-only. When the
         * values are structured rather than scalar, this is [ArgType.ANY] and the value's keys
         * are described by [schema].
         */
        val mapValueType: ArgType? = null,
        /** For arguments whose accepted values are the constants of an enum (usually a Bukkit
         *  enum such as org.bukkit.SoundCategory). The wiki links to the enum's JavaDoc rather
         *  than listing constants, which drift between Minecraft versions. When set and
         *  [choices] is empty, choices are derived from the enum's constants at runtime. */
        val enumClass: KClass<out Enum<*>>? = null,
        /**
         * For [ArgType.MAP] args whose user-chosen keys are enum constant names: the enum the
         * keys belong to. Documented in the same way as [enumClass].
         */
        val mapKeyEnumClass: KClass<out Enum<*>>? = null,
        /**
         * For [ArgType.MAP] args whose values are enum constant names: the enum the values
         * belong to. Documented in the same way as [enumClass].
         */
        val mapValueEnumClass: KClass<out Enum<*>>? = null
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
        schema: KClass<*>? = null,
        example: Any? = null,
        mapKeyType: ArgType? = null,
        mapValueType: ArgType? = null,
        enumClass: KClass<out Enum<*>>? = null,
        mapKeyEnumClass: KClass<out Enum<*>>? = null,
        mapValueEnumClass: KClass<out Enum<*>>? = null
    ) {
        require(
            listOf(name),
            message,
            description,
            type,
            choices,
            schema,
            example,
            mapKeyType,
            mapValueType,
            enumClass,
            mapKeyEnumClass,
            mapValueEnumClass
        )
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
        schema: KClass<*>? = null,
        example: Any? = null,
        mapKeyType: ArgType? = null,
        mapValueType: ArgType? = null,
        enumClass: KClass<out Enum<*>>? = null,
        mapKeyEnumClass: KClass<out Enum<*>>? = null,
        mapValueEnumClass: KClass<out Enum<*>>? = null
    ) {
        require(
            names,
            message,
            { get(it) },
            { true },
            description,
            type,
            choices,
            schema,
            example,
            mapKeyType,
            mapValueType,
            enumClass,
            mapKeyEnumClass,
            mapValueEnumClass
        )
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
        schema: KClass<*>? = null,
        example: Any? = null,
        mapKeyType: ArgType? = null,
        mapValueType: ArgType? = null,
        enumClass: KClass<out Enum<*>>? = null,
        mapKeyEnumClass: KClass<out Enum<*>>? = null,
        mapValueEnumClass: KClass<out Enum<*>>? = null
    ) {
        arguments += RequiredArgument(
            names,
            message,
            getter,
            predicate,
            description,
            type,
            choices,
            schema,
            example,
            mapKeyType,
            mapValueType,
            enumClass,
            mapKeyEnumClass,
            mapValueEnumClass
        )
    }

    @JvmOverloads
    fun optional(
        name: String,
        description: String = "",
        type: ArgType = ArgType.ANY,
        default: String? = null,
        choices: List<String> = emptyList(),
        schema: KClass<*>? = null,
        example: Any? = null,
        mapKeyType: ArgType? = null,
        mapValueType: ArgType? = null,
        enumClass: KClass<out Enum<*>>? = null,
        mapKeyEnumClass: KClass<out Enum<*>>? = null,
        mapValueEnumClass: KClass<out Enum<*>>? = null
    ) {
        arguments += OptionalArgument(
            listOf(name),
            description,
            type,
            default,
            choices,
            schema,
            example,
            mapKeyType,
            mapValueType,
            enumClass,
            mapKeyEnumClass,
            mapValueEnumClass
        )
    }

    @JvmOverloads
    fun optional(
        names: Collection<String>,
        description: String = "",
        type: ArgType = ArgType.ANY,
        default: String? = null,
        choices: List<String> = emptyList(),
        schema: KClass<*>? = null,
        example: Any? = null,
        mapKeyType: ArgType? = null,
        mapValueType: ArgType? = null,
        enumClass: KClass<out Enum<*>>? = null,
        mapKeyEnumClass: KClass<out Enum<*>>? = null,
        mapValueEnumClass: KClass<out Enum<*>>? = null
    ) {
        arguments += OptionalArgument(
            names,
            description,
            type,
            default,
            choices,
            schema,
            example,
            mapKeyType,
            mapValueType,
            enumClass,
            mapKeyEnumClass,
            mapValueEnumClass
        )
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
        choices: List<String> = emptyList(),
        example: Any? = null,
        mapKeyType: ArgType? = null,
        mapValueType: ArgType? = null,
        enumClass: KClass<out Enum<*>>? = null,
        mapKeyEnumClass: KClass<out Enum<*>>? = null,
        mapValueEnumClass: KClass<out Enum<*>>? = null
    ) {
        val arg = arguments
            .filterIsInstance<RequiredArgument<*>>()
            .lastOrNull { name in it.argNames }
            ?: return

        val resolvedEnumClass = enumClass ?: arg.meta.enumClass

        arg.meta = arg.meta.copy(
            description = description,
            type = type,
            choices = resolveChoices(choices, resolvedEnumClass),
            example = example ?: arg.meta.example,
            mapKeyType = mapKeyType ?: arg.meta.mapKeyType,
            mapValueType = mapValueType ?: arg.meta.mapValueType,
            enumClass = resolvedEnumClass,
            mapKeyEnumClass = mapKeyEnumClass ?: arg.meta.mapKeyEnumClass,
            mapValueEnumClass = mapValueEnumClass ?: arg.meta.mapValueEnumClass
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

/**
 * Explicitly passed [choices] always win; otherwise derive the choices from the constants
 * of [enumClass], if one was declared.
 */
private fun resolveChoices(
    choices: List<String>,
    enumClass: KClass<out Enum<*>>?
): List<String> {
    if (choices.isNotEmpty() || enumClass == null) {
        return choices
    }

    return enumClass.java.enumConstants?.map { it.name } ?: emptyList()
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
    schema: KClass<*>? = null,
    example: Any? = null,
    mapKeyType: ArgType? = null,
    mapValueType: ArgType? = null,
    enumClass: KClass<out Enum<*>>? = null,
    mapKeyEnumClass: KClass<out Enum<*>>? = null,
    mapValueEnumClass: KClass<out Enum<*>>? = null
) : ConfigArgument {
    override var meta: ArgumentMeta.Regular = ArgumentMeta.Regular(
        names = argNames,
        description = description,
        type = type,
        required = true,
        default = null,
        choices = resolveChoices(choices, enumClass),
        schema = schema,
        example = example,
        mapKeyType = mapKeyType,
        mapValueType = mapValueType,
        enumClass = enumClass,
        mapKeyEnumClass = mapKeyEnumClass,
        mapValueEnumClass = mapValueEnumClass
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
    schema: KClass<*>? = null,
    example: Any? = null,
    mapKeyType: ArgType? = null,
    mapValueType: ArgType? = null,
    enumClass: KClass<out Enum<*>>? = null,
    mapKeyEnumClass: KClass<out Enum<*>>? = null,
    mapValueEnumClass: KClass<out Enum<*>>? = null
) : ConfigArgument {
    override val meta = ArgumentMeta.Regular(
        names = names,
        description = description,
        type = type,
        required = false,
        default = default,
        choices = resolveChoices(choices, enumClass),
        schema = schema,
        example = example,
        mapKeyType = mapKeyType,
        mapValueType = mapValueType,
        enumClass = enumClass,
        mapKeyEnumClass = mapKeyEnumClass,
        mapValueEnumClass = mapValueEnumClass
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
