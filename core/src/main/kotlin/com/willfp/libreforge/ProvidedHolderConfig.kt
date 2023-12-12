package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.copy
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.GlobalDispatcher.dispatcher

/**
 * A [config] that uses a provided [holder] as a source of placeholders.
 *
 * This allows for item placeholders to be used in config values.
 */
private class ProvidedHolderConfig(
    private val config: Config,
    private val holder: ProvidedHolder
) : Config by config {
    override fun getDoubleFromExpression(path: String, context: PlaceholderContext): Double {
        return NumberUtils.evaluateExpression(
            this.getString(path),
            context.withInjectableContext(config).copy(item = holder.getProvider())
        )
    }

    override fun getFormattedStringOrNull(path: String, context: PlaceholderContext): String? {
        val string = this.getStringOrNull(path) ?: return null
        return string.formatEco(context.withInjectableContext(config).copy(item = holder.getProvider()))
    }

    override fun getFormattedStringsOrNull(path: String, context: PlaceholderContext): List<String>? {
        val strings = this.getStringsOrNull(path) ?: return null
        return strings.formatEco(context.withInjectableContext(config).copy(item = holder.getProvider()))
    }

    // Kotlin Delegation doesn't seem to like these methods?
    // I'm sure there's a better way of doing this but debugging this was a pain in the dick
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
}

fun Config.applyHolder(providedHolder: ProvidedHolder, dispatcher: Dispatcher<*>): Config =
    ProvidedHolderConfig(this, providedHolder).apply {
        injectPlaceholders(*providedHolder.generatePlaceholders(dispatcher).mapToPlaceholders())
    }
