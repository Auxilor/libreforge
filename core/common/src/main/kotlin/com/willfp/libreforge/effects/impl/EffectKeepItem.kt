package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.SlotTypes
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectKeepItem : Effect<NoCompileData>("keep_item") {
    override val description = "Keeps an item in the player's inventory when they die, instead of it being dropped."
    override val categories = setOf("player", "inventory")

    override val arguments = arguments {
        optional(
            "slot",
            description = "The inventory slot of the item to keep, e.g. mainhand or slot_0. If omitted, the item providing this effect is kept.",
            type = ArgType.STRING
        )
    }

    private val players = listMap<UUID, Triple<UUID, Config, ItemStack?>>()
    private val savedItems = listMap<UUID, ItemStack>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        players[dispatcher.uuid].add(Triple(identifiers.uuid, config, (holder.provider as? ItemStack)?.clone()))
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        players[dispatcher.uuid].removeAll { it.first == identifiers.uuid }
    }

    @EventHandler(ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.player

        for ((_, config, providedItem) in players[player.uniqueId]) {
            val slotName = config.getString("slot")

            val slots = if (slotName.isNotEmpty()) {
                SlotTypes[slotName]?.getItemSlots(player) ?: continue
            } else {
                if (providedItem == null) continue

                val slot = (0..40).firstOrNull { player.inventory.getItem(it)?.isSimilar(providedItem) == true }
                    ?: continue

                listOf(slot)
            }

            for (slot in slots) {
                val item = player.inventory.getItem(slot)

                if (item.isEcoEmpty) {
                    continue
                }

                event.drops.remove(item)
                savedItems[player.uniqueId].add(item!!.clone())
                player.inventory.setItem(slot, null)
            }
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val items = savedItems.remove(player.uniqueId) ?: return

        for (item in items) {
            player.inventory.addItem(item)
        }
    }
}
