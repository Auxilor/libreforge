package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.NumberUtils
import org.bukkit.entity.Player
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
            PlaceholderContext(
                null,
                holder.provider as? ItemStack,
                PlaceholderManager.EMPTY_INJECTABLE,
                emptyList()
            )
        )
    }

    override fun getDoubleFromExpression(path: String, player: Player?): Double {
        return NumberUtils.evaluateExpression(
            this.getString(path),
            PlaceholderContext(
                player,
                holder.provider as? ItemStack,
                PlaceholderManager.EMPTY_INJECTABLE,
                emptyList()
            )
        )
    }

    override fun getDoubleFromExpression(
        path: String,
        player: Player?,
        additionalPlayers: MutableCollection<AdditionalPlayer>
    ): Double {
        return NumberUtils.evaluateExpression(
            this.getString(path),
            PlaceholderContext(
                player,
                holder.provider as? ItemStack,
                PlaceholderManager.EMPTY_INJECTABLE,
                additionalPlayers
            )
        )
    }
}

fun Config.withHolder(providedHolder: ProvidedHolder): Config =
    ProvidedHolderConfig(this, providedHolder)
