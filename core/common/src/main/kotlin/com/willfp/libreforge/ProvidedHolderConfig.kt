package com.willfp.libreforge

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.copy
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import java.util.concurrent.ConcurrentHashMap

/**
 * A [config] that uses a provided [holder] as a source of placeholders.
 *
 * This allows for item placeholders to be used in config values. The holder's placeholders are
 * kept here rather than on the shared [config], so effects that keep this config and read it later
 * always see their own holder's values.
 */
private class ProvidedHolderConfig(
    private val config: Config,
    private val holder: ProvidedHolder
) : Config {
    private val injections = ConcurrentHashMap<String, InjectablePlaceholder>()

    private fun wrap(child: Config): Config =
        ProvidedHolderConfig(child, holder).also { it.addInjectablePlaceholder(injections.values.toMutableList()) }

    override fun getDoubleFromExpression(path: String, context: PlaceholderContext): Double {
        return NumberUtils.evaluateExpression(
            this.getString(path),
            context.withInjectableContext(this).copy(item = holder.getProvider())
        )
    }

    override fun getFormattedStringOrNull(path: String, context: PlaceholderContext): String? {
        val string = this.getStringOrNull(path) ?: return null
        return string.formatEco(context.withInjectableContext(this).copy(item = holder.getProvider()))
    }

    override fun getFormattedStringsOrNull(path: String, context: PlaceholderContext): List<String>? {
        val strings = this.getStringsOrNull(path) ?: return null
        return strings.formatEco(context.withInjectableContext(this).copy(item = holder.getProvider()))
    }

    override fun clone(): Config = wrap(config.clone())

    override fun toPlaintext(): String = config.toPlaintext()

    override fun has(path: String): Boolean = config.has(path)

    override fun getKeys(deep: Boolean): List<String> = config.getKeys(deep)

    override fun get(path: String): Any? = config.get(path)

    override fun set(path: String, obj: Any?) = config.set(path, obj)

    override fun getSubsectionOrNull(path: String): Config? = config.getSubsectionOrNull(path)?.let { wrap(it) }

    override fun getIntOrNull(path: String): Int? = config.getIntOrNull(path)

    override fun getIntsOrNull(path: String): List<Int>? = config.getIntsOrNull(path)

    override fun getBoolOrNull(path: String): Boolean? = config.getBoolOrNull(path)

    override fun getBoolsOrNull(path: String): List<Boolean>? = config.getBoolsOrNull(path)

    override fun getStringOrNull(path: String, format: Boolean, option: StringUtils.FormatOption): String? =
        config.getStringOrNull(path, format, option)

    override fun getStringsOrNull(
        path: String, format: Boolean, option: StringUtils.FormatOption
    ): List<String>? = config.getStringsOrNull(path, format, option)

    override fun getDoubleOrNull(path: String): Double? = config.getDoubleOrNull(path)

    override fun getDoublesOrNull(path: String): List<Double>? = config.getDoublesOrNull(path)

    override fun getSubsectionsOrNull(path: String): List<Config>? =
        config.getSubsectionsOrNull(path)?.map { wrap(it) }

    override fun getType(): ConfigType = config.type

    override fun injectPlaceholders(vararg placeholders: InjectablePlaceholder) =
        addInjectablePlaceholder(placeholders.toMutableList())

    override fun injectPlaceholders(vararg placeholders: StaticPlaceholder) =
        addInjectablePlaceholder(placeholders.toMutableList<InjectablePlaceholder>())

    override fun addInjectablePlaceholder(placeholders: MutableIterable<InjectablePlaceholder>) {
        for (placeholder in placeholders) {
            injections[placeholder.patternString] = placeholder
        }
    }

    override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
        val base = config.placeholderInjections

        if (injections.isEmpty()) {
            return base
        }

        return base.filter { !injections.containsKey(it.patternString) } + injections.values
    }

    override fun clearInjectedPlaceholders() =
        injections.clear()
}

fun Config.applyHolder(providedHolder: ProvidedHolder, dispatcher: Dispatcher<*>): Config =
    ProvidedHolderConfig(this, providedHolder).apply {
        addInjectablePlaceholder(
            HolderStates.cachedPlaceholders(dispatcher, providedHolder.holder)
                ?: providedHolder.generatePlaceholders(dispatcher).mapToPlaceholders()
        )
    }
