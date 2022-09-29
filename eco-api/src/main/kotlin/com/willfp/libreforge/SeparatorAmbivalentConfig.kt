package com.willfp.libreforge

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import java.util.*

private class SeparatorAmbivalentConfig(
    private val config: Config
) : Config {
    private inline fun <reified T> preprocess(path: String, getter: (String) -> T): T? {
        return preprocess(path, getter, { it == null }, null)
    }

    private inline fun <reified T> preprocess(
        path: String,
        getter: (String) -> T,
        invalidator: (T) -> Boolean,
        invalid: T?
    ): T? {
        val hyphen = path.lowercase().replace('_', '-')

        val underscore = path.lowercase().replace('-', '_')

        val unspaced = path.lowercase().replace("-", "")
            .replace("_", "")

        val camelcase = underscore.toCamelCase()

        for (formattedPath in arrayOf(hyphen, underscore, unspaced, camelcase)) {
            val found = getter(formattedPath)

            if (!invalidator(found)) {
                return found
            }
        }

        return invalid
    }

    override fun clone(): Config = SeparatorAmbivalentConfig(config.clone())

    override fun toPlaintext(): String = config.toPlaintext()

    override fun has(path: String): Boolean {
        return preprocess(path, { config.has(it) }, { !it }, false) ?: false
    }

    override fun getKeys(deep: Boolean): List<String> =
        config.getKeys(deep)

    override fun get(path: String): Any? =
        preprocess(path) { config.get(it) }

    override fun set(path: String, obj: Any?) =
        config.set(path, obj)

    override fun getSubsectionOrNull(path: String): Config? =
        preprocess(path) {
            val cfg = config.getSubsectionOrNull(it)
            if (cfg == null) null else SeparatorAmbivalentConfig(cfg)
        }

    override fun getIntOrNull(path: String): Int? =
        preprocess(path) { config.getIntOrNull(it) }

    override fun getIntsOrNull(path: String): List<Int>? =
        preprocess(path) { config.getIntsOrNull(it) }

    override fun getBoolOrNull(path: String): Boolean? =
        preprocess(path) { config.getBoolOrNull(it) }

    override fun getBoolsOrNull(path: String): List<Boolean>? =
        preprocess(path) { config.getBoolsOrNull(it) }

    override fun getStringOrNull(path: String, format: Boolean, option: StringUtils.FormatOption): String? =
        preprocess(path) { config.getStringOrNull(it, format, option) }

    override fun getStringsOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): List<String>? = preprocess(path) { config.getStringsOrNull(it, format, option) }

    override fun getDoubleOrNull(path: String): Double? =
        preprocess(path) { config.getDoubleOrNull(it) }

    override fun getDoublesOrNull(path: String): List<Double>? =
        preprocess(path) { config.getDoublesOrNull(it) }

    override fun getSubsectionsOrNull(path: String): List<Config>? =
        preprocess(path) { config.getSubsectionsOrNull(it)?.map { cfg -> SeparatorAmbivalentConfig(cfg) } }

    override fun getType(): ConfigType = config.type

    override fun addInjectablePlaceholder(placeholders: Iterable<InjectablePlaceholder>) =
        config.addInjectablePlaceholder(placeholders)

    override fun clearInjectedPlaceholders() =
        config.clearInjectedPlaceholders()

    override fun getPlaceholderInjections(): List<InjectablePlaceholder> =
        config.placeholderInjections

    override fun injectPlaceholders(vararg placeholders: StaticPlaceholder) =
        config.injectPlaceholders(*placeholders)

    override fun injectPlaceholders(vararg placeholders: InjectablePlaceholder) =
        config.injectPlaceholders(*placeholders)
}

fun Config.separatorAmbivalent(): Config = SeparatorAmbivalentConfig(this)

fun Config.getIntFromExpression(path: String, data: TriggerData): Int {
    val player = data.player
    val additional = mutableListOf<AdditionalPlayer>()

    if (data.victim is Player) {
        additional += AdditionalPlayer(data.victim, "victim")
    }
    return getIntFromExpression(path, player, additional)
}

fun Config.getDoubleFromExpression(path: String, data: TriggerData): Double {
    val player = data.player
    val additional = mutableListOf<AdditionalPlayer>()

    if (data.victim is Player) {
        additional += AdditionalPlayer(data.victim, "victim")
    }
    return getDoubleFromExpression(path, player, additional)
}

// Seamlessly stolen from: https://stackoverflow.com/a/1144014/11427550
private fun String.toCamelCase(): String {
    val builder = StringBuilder()

    for (oneString in this.lowercase().split("_")) {
        builder.append(oneString.substring(0, 1).uppercase(Locale.getDefault()))
        builder.append(oneString.substring(1))
    }

    return builder.toString()
}
