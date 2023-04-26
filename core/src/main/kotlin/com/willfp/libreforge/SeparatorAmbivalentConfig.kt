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
import org.bukkit.inventory.ItemStack
import java.util.Locale

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
        val unspaced = path.lowercase().replace("-", "").replace("_", "")
        val camelcase = underscore.toCamelCase()

        val formattedPaths = arrayOf(hyphen, underscore, unspaced, camelcase)

        return formattedPaths.map(getter)
            .firstOrNull { !invalidator(it) } ?: invalid
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

    override fun addInjectablePlaceholder(placeholders: Iterable<InjectablePlaceholder>) =
        config.addInjectablePlaceholder(placeholders)

    override fun clearInjectedPlaceholders() = config.clearInjectedPlaceholders()

    override fun getPlaceholderInjections(): List<InjectablePlaceholder> = config.placeholderInjections

    override fun injectPlaceholders(vararg placeholders: StaticPlaceholder) = config.injectPlaceholders(*placeholders)

    override fun injectPlaceholders(vararg placeholders: InjectablePlaceholder) =
        config.injectPlaceholders(*placeholders)
}

fun Config.separatorAmbivalent(): Config =
    if (this is SeparatorAmbivalentConfig) this else SeparatorAmbivalentConfig(this)

@Deprecated(
    "Use toPlaceholderContext instead",
    ReplaceWith("this.toPlaceholderContext(data)"),
    DeprecationLevel.ERROR
)
@Suppress("DEPRECATION")
fun Config.toMathContext(data: TriggerData? = null): com.willfp.eco.core.math.MathContext {
    return this.toPlaceholderContext(data).toMathContext()
}

fun Config.toPlaceholderContext(data: TriggerData? = null): PlaceholderContext {
    val additionalPlayers = mutableListOf<AdditionalPlayer>()

    data?.let {
        if (it.victim is Player) {
            additionalPlayers += AdditionalPlayer(it.victim, "victim")
        }
    }

    return PlaceholderContext(
        data?._originalPlayer,
        data?.holder?.provider as? ItemStack,
        this,
        additionalPlayers
    )
}


fun Config.getIntFromExpression(path: String, data: TriggerData?) = NumberUtils.evaluateExpression(
    this.getString(path), this.toPlaceholderContext(data)
).toInt()

fun Config.getDoubleFromExpression(path: String, data: TriggerData?) = NumberUtils.evaluateExpression(
    this.getString(path), this.toPlaceholderContext(data)
)

private fun String.toCamelCase(): String {
    val words = this.lowercase().split("_")

    return words.first() + words.drop(1).joinToString("") { it.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(
            Locale.getDefault()
        ) else char.toString()
    }}
}
