package com.willfp.libreforge

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

private class SeparatorAmbivalentConfig(
    private val config: Config
) : Config {
    private val resolvedPaths = HashMap<String, String>()

    private inline fun <reified T> preprocess(path: String, getter: (String) -> T): T? {
        return preprocess(path, getter, { it == null }, null)
    }

    private inline fun <reified T> preprocess(
        path: String,
        getter: (String) -> T,
        invalidator: (T) -> Boolean,
        invalid: T?
    ): T? {
        resolvedPaths[path]?.let { resolved ->
            val result = getter(resolved)
            if (!invalidator(result)) return result
        }

        val lower = path.lowercase()
        val hyphen = lower.replace('_', '-')
        val underscore = lower.replace('-', '_')

        getter(hyphen).let { if (!invalidator(it)) { resolvedPaths[path] = hyphen; return it } }
        getter(underscore).let { if (!invalidator(it)) { resolvedPaths[path] = underscore; return it } }

        val unspaced = lower.replace("-", "").replace("_", "")
        getter(unspaced).let { if (!invalidator(it)) { resolvedPaths[path] = unspaced; return it } }

        val camelcase = underscore.toCamelCase()
        getter(camelcase).let { if (!invalidator(it)) { resolvedPaths[path] = camelcase; return it } }
        return invalid
    }

    override fun clone(): Config = config.clone().separatorAmbivalent()

    override fun toPlaintext(): String = config.toPlaintext()

    override fun has(path: String): Boolean {
        return preprocess(path, { config.has(it) }, { !it }, false) ?: false
    }

    override fun getKeys(deep: Boolean): List<String> = config.getKeys(deep)

    override fun get(path: String): Any? = preprocess(path) { config.get(it) }

    override fun set(path: String, obj: Any?) = config.set(path, obj)

    override fun getSubsectionOrNull(path: String): Config? = preprocess(path) {
        config.getSubsectionOrNull(it)?.separatorAmbivalent()
    }

    override fun getIntOrNull(path: String): Int? = preprocess(path) { config.getIntOrNull(it) }

    override fun getIntsOrNull(path: String): List<Int>? = preprocess(path) { config.getIntsOrNull(it) }

    override fun getBoolOrNull(path: String): Boolean? = preprocess(path) { config.getBoolOrNull(it) }

    override fun getBoolsOrNull(path: String): List<Boolean>? = preprocess(path) { config.getBoolsOrNull(it) }

    override fun getStringOrNull(path: String, format: Boolean, option: StringUtils.FormatOption): String? =
        preprocess(path) { config.getStringOrNull(it, format, option) }

    override fun getStringsOrNull(
        path: String, format: Boolean, option: StringUtils.FormatOption
    ): List<String>? = preprocess(path) { config.getStringsOrNull(it, format, option) }

    override fun getDoubleOrNull(path: String): Double? = preprocess(path) { config.getDoubleOrNull(it) }

    override fun getDoublesOrNull(path: String): List<Double>? = preprocess(path) { config.getDoublesOrNull(it) }

    override fun getSubsectionsOrNull(path: String): List<Config>? =
        preprocess(path) { config.getSubsectionsOrNull(it)?.map { cfg -> cfg.separatorAmbivalent() } }

    override fun getType(): ConfigType = config.type

    // Adding them all, because I'm lazy
    override fun injectPlaceholders(vararg placeholders: InjectablePlaceholder) =
        config.injectPlaceholders(*placeholders)

    override fun injectPlaceholders(vararg placeholders: StaticPlaceholder) =
        config.injectPlaceholders(*placeholders)

    override fun addInjectablePlaceholder(placeholders: MutableIterable<InjectablePlaceholder>) =
        config.addInjectablePlaceholder(placeholders)

    override fun getPlaceholderInjections(): List<InjectablePlaceholder> =
        config.placeholderInjections

    override fun clearInjectedPlaceholders() =
        config.clearInjectedPlaceholders()

    override fun equals(other: Any?) =
        config == other

    override fun hashCode() =
        config.hashCode()
}

fun Config.separatorAmbivalent(): Config =
    this as? SeparatorAmbivalentConfig ?: SeparatorAmbivalentConfig(this)

fun Config.toPlaceholderContext(data: TriggerData? = null): PlaceholderContext {
    val additionalPlayers = if (data?.victim is Player) {
        listOf(AdditionalPlayer(data.victim, "victim"))
    } else {
        emptyList()
    }

    return PlaceholderContext(
        data?.originalPlayer,
        data?.holder?.getProvider() ?: data?.item,
        this,
        additionalPlayers
    )
}

fun Config.getStrings(pluralPath: String, singularPath: String): List<String> =
    this.getStrings(pluralPath) + this.getString(singularPath)

fun Config.getFormattedStrings(
    pluralPath: String,
    singularPath: String,
    data: TriggerData? = null
) = this.getFormattedStrings(pluralPath, data) + this.getFormattedString(singularPath, data)

fun Config.getIntFromExpression(path: String, data: TriggerData?) = NumberUtils.evaluateExpression(
    this.getString(path), this.toPlaceholderContext(data)
).toInt()

fun Config.getDoubleFromExpression(path: String, data: TriggerData?) = NumberUtils.evaluateExpression(
    this.getString(path), this.toPlaceholderContext(data)
)

fun Config.getFormattedString(path: String, data: TriggerData?) =
    this.getFormattedString(path, this.toPlaceholderContext(data))

fun Config.getFormattedStrings(path: String, data: TriggerData?) =
    this.getFormattedStrings(path, this.toPlaceholderContext(data))

inline fun <reified T : Any> Config.getOrNull(path: String, getter: Config.(String) -> T) =
    this.getOrElse(path, null, getter)

inline fun <reified T> Config.getOrElse(path: String, default: T, getter: Config.(String) -> T) =
    if (this.has(path)) this.getter(path) else default

private fun String.toCamelCase(): String {
    return buildString(this@toCamelCase.length) {
        var capitalizeNext = false
        for (char in this@toCamelCase) {
            if (char == '_') {
                capitalizeNext = true
            } else if (capitalizeNext) {
                append(char.uppercaseChar())
                capitalizeNext = false
            } else {
                append(char)
            }
        }
    }
}