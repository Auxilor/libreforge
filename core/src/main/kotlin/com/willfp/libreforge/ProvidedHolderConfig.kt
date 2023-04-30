package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.copy
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEco
import org.bukkit.inventory.ItemStack

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
            context.withInjectableContext(config).copy(item = holder.provider as? ItemStack)
        )
    }

    override fun getFormattedStringOrNull(path: String, context: PlaceholderContext): String? {
        val string = this.getStringOrNull(path) ?: return null
        return string.formatEco(context.withInjectableContext(config).copy(item = holder.provider as? ItemStack))
    }

    override fun getFormattedStringsOrNull(path: String, context: PlaceholderContext): List<String>? {
        val strings = this.getStringsOrNull(path) ?: return null
        return strings.formatEco(context.withInjectableContext(config).copy(item = holder.provider as? ItemStack))
    }
}

fun Config.applyHolder(providedHolder: ProvidedHolder): Config =
    ProvidedHolderConfig(this, providedHolder).apply {
        injectPlaceholders(*providedHolder.generatePlaceholders().mapToPlaceholders())
    }
