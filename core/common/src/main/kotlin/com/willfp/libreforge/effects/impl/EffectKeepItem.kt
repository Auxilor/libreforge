package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.map.concurrentListMap
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.ProviderBinding
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.plugin
import com.willfp.libreforge.slot.SlotTypes
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectKeepItem : Effect<NoCompileData>("keep_item") {
    override val description = "Keeps an item in the player's inventory when they die, instead of it being dropped."
    override val categories = setOf("player", "inventory")

    override val providerBinding = ProviderBinding.ITEM

    override val arguments = arguments {
        optional(
            "slot",
            description = "The inventory slot of the item to keep, e.g. mainhand or slot_0. If omitted, the item providing this effect is kept.",
            type = ArgType.STRING,
            example = "mainhand"
        )
        optional(
            "chance",
            description = "The percent chance (0-100) that each item is kept on death. If omitted, the item is always kept. Supports expressions and placeholders.",
            type = ArgType.EXPRESSION,
            example = "50"
        )
    }

    private val players = concurrentListMap<UUID, Triple<UUID, Config, ItemStack?>>()

    private val savedItemsKey by lazy {
        PersistentDataKey(
            plugin.namespacedKeyFactory.create("keep_item_slots"),
            PersistentDataKeyType.STRING_LIST,
            emptyList()
        )
    }

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
        if (event.keepInventory) {
            return
        }

        val player = event.player
        val newEntries = mutableListOf<String>()

        for ((_, config, providedItem) in players[player.uniqueId]) {
            for (slot in player.slotsToKeep(config, providedItem)) {
                val item = player.inventory.getItem(slot)

                if (item.isEcoEmpty) {
                    continue
                }

                if (!player.rollKeepChance(config)) {
                    continue
                }

                event.drops.remove(item)

                if (Prerequisite.HAS_PAPER.isMet) {
                    event.itemsToKeep += item!!
                } else {
                    newEntries.add("$slot:${Items.toSNBT(item!!)}")
                    player.inventory.setItem(slot, null)
                }
            }
        }

        if (newEntries.isNotEmpty()) {
            val existing = player.profile.read(savedItemsKey)
            player.profile.write(savedItemsKey, existing + newEntries)
        }
    }

    private fun Player.rollKeepChance(config: Config): Boolean {
        if (!config.has("chance")) {
            return true
        }

        return NumberUtils.randFloat(0.0, 100.0) <= config.getDoubleFromExpression("chance", this)
    }

    private fun Player.slotsToKeep(config: Config, providedItem: ItemStack?): List<Int> {
        val slotName = config.getString("slot")

        if (slotName.isNotEmpty()) {
            return SlotTypes[slotName]?.getItemSlots(this).orEmpty()
        }

        val similarSlots = (0..40).filter {
            providedItem != null && inventory.getItem(it)?.isSimilar(providedItem) == true
        }

        return listOfNotNull(
            similarSlots.firstOrNull { inventory.getItem(it)?.amount == providedItem?.amount }
                ?: similarSlots.firstOrNull()
        )
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        plugin.scheduler.on(event.player).run { restoreItems(event.player) }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.on(event.player).run { restoreItems(event.player) }
    }

    private fun restoreItems(player: Player) {
        val entries = player.profile.read(savedItemsKey)
        if (entries.isEmpty()) return

        player.profile.write(savedItemsKey, emptyList())

        for (entry in entries) {
            val colonIndex = entry.indexOf(':')
            if (colonIndex == -1) continue
            val slot = entry.substring(0, colonIndex).toIntOrNull() ?: continue
            val item = Items.fromSNBT(entry.substring(colonIndex + 1)) ?: continue

            if (player.inventory.getItem(slot).isEcoEmpty) {
                player.inventory.setItem(slot, item)
            } else {
                player.inventory.addItem(item)
            }
        }
    }
}
