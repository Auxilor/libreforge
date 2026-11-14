package com.willfp.libreforge.placeholders.impl

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimplePlaceholder
import com.willfp.libreforge.BlankHolder
import com.willfp.libreforge.HolderStates
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.Regions
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.placeholders.CustomPlaceholder
import com.willfp.libreforge.toDispatcher
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class ConditionalCustomPlaceholder(
    id: String,
    config: Config,
    private val plugin: EcoPlugin
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

    private val pendingRefreshes: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    override val placeholder: RegistrablePlaceholder = object : SimplePlaceholder(plugin, id) {
        override fun getValue(args: String, ctx: PlaceholderContext): String? {
            val player = ctx.player ?: run {
                return defaultExpr?.let { parseValue(it, ctx) }
            }

            val holder = BlankItemHolder(ctx.itemStack)

            if (!Regions.owns(player)) {
                resendIfKeptResultsChanged(player, holder)
            }

            val value = values
                .firstOrNull { it.conditions.areMet(player.toDispatcher(), holder) }
                ?.expr ?: return defaultExpr?.let { parseValue(it, ctx) }

            return parseValue(value, ctx)
        }
    }

    private fun resendIfKeptResultsChanged(player: Player, holder: ProvidedHolder) {
        if (!pendingRefreshes.add(player.uniqueId)) {
            return
        }

        plugin.scheduler.on(player).run {
            pendingRefreshes.remove(player.uniqueId)

            val dispatcher = player.toDispatcher()
            val changedBlocks = values.flatMap { it.conditions }.filter { block ->
                HolderStates.conditionResult(dispatcher, block) != block.isMet(dispatcher, holder)
            }

            if (player.isOnline && changedBlocks.isNotEmpty()) {
                player.updateInventory()
            }
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
