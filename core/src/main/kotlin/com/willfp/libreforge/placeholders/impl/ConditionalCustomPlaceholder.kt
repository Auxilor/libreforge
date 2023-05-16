package com.willfp.libreforge.placeholders.impl

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimplePlaceholder
import com.willfp.libreforge.BlankHolder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.placeholders.CustomPlaceholder
import org.bukkit.inventory.ItemStack

class ConditionalCustomPlaceholder(
    id: String,
    private val config: Config,
    plugin: EcoPlugin
) : CustomPlaceholder(id) {
    private val defaultExpr = config.getStringOrNull("default")

    private val values = config.getSubsections("values").map {
        ConditionalPlaceholderValue(
            Conditions.compile(
                it.getSubsections("conditions"),
                ViolationContext(plugin, "conditional placeholder $id")
            ),
            it.getString("value")
        )
    }

    override val placeholder: RegistrablePlaceholder = object : SimplePlaceholder(plugin, id) {
        override fun getValue(args: String, ctx: PlaceholderContext): String? {
            val player = ctx.player ?: run {
                return defaultExpr?.let { parseValue(it, ctx) }
            }

            val holder = BlankItemHolder(ctx.itemStack)

            val value = values
                .firstOrNull { it.conditions.areMet(player, holder) }
                ?.expr ?: return defaultExpr?.let { parseValue(it, ctx) }

            return parseValue(value, ctx)
        }
    }

    private data class ConditionalPlaceholderValue(
        val conditions: ConditionList,
        val expr: String
    )

    private class BlankItemHolder(
        override val provider: ItemStack?
    ) : ProvidedHolder {
        override val holder = BlankHolder
    }
}
