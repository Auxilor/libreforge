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
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectKeepItem : Effect<NoCompileData>("keep_item") {
    private const val MAX_KEEP_CHANCE = 100.0

    override val description =
        "Keeps an item in the player's inventory when they die, instead of it being dropped."

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

    private val players =
        concurrentListMap<UUID, Triple<UUID, Config, ItemStack?>>()

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
        players[dispatcher.uuid].add(
            Triple(
                identifiers.uuid,
                config,
                (holder.provider as? ItemStack)?.clone()
            )
        )
    }

    override fun onDisable(
        dispatcher: Dispatcher<*>,
        identifiers: Identifiers,
        holder: ProvidedHolder
    ) {
        players[dispatcher.uuid].removeAll { entry ->
            entry.first == identifiers.uuid
        }
    }

    @EventHandler(
        priority = EventPriority.LOW,
        ignoreCancelled = true
    )
    fun onDeath(event: PlayerDeathEvent) {
        if (event.keepInventory) {
            return
        }

        val player = event.player
        val attemptedSlots = mutableSetOf<Int>()

        val newEntries = players[player.uniqueId].flatMap { (_, config, providedItem) ->
            player.slotsToKeep(config, providedItem, attemptedSlots)
                .filter(attemptedSlots::add)
                .mapNotNull { slot -> event.keepItem(slot, config) }
        }

        if (newEntries.isNotEmpty()) {
            val existing = player.profile.read(savedItemsKey)
            player.profile.write(savedItemsKey, existing + newEntries)
        }
    }

    private fun PlayerDeathEvent.keepItem(
        slot: Int,
        config: Config
    ): String? {
        val item = player.inventory.getItem(slot)?.takeIf { slotItem ->
            !slotItem.isEcoEmpty && player.rollKeepChance(config) && drops.remove(slotItem)
        } ?: return null

        return if (Prerequisite.HAS_PAPER.isMet) {
            itemsToKeep += item
            null
        } else {
            player.inventory.setItem(slot, null)
            "$slot:${Items.toSNBT(item)}"
        }
    }

    private fun Player.rollKeepChance(config: Config): Boolean {
        if (!config.has("chance")) {
            return true
        }

        return NumberUtils.randFloat(0.0, MAX_KEEP_CHANCE) <=
                config.getDoubleFromExpression("chance", this)
    }

    private fun Player.slotsToKeep(
        config: Config,
        providedItem: ItemStack?,
        attemptedSlots: Set<Int>
    ): List<Int> {
        val slotName = config.getString("slot")

        if (slotName.isNotEmpty()) {
            return SlotTypes[slotName]?.getItemSlots(this).orEmpty()
        }

        val similarSlots = (0..40).filter { slot ->
            slot !in attemptedSlots &&
                    providedItem != null &&
                    inventory.getItem(slot)?.isSimilar(providedItem) == true
        }

        return listOfNotNull(
            similarSlots.firstOrNull { slot ->
                inventory.getItem(slot)?.amount == providedItem?.amount
            } ?: similarSlots.firstOrNull()
        )
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        plugin.scheduler.on(event.player).run {
            restoreItems(event.player)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.on(event.player).run {
            restoreItems(event.player)
        }
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
