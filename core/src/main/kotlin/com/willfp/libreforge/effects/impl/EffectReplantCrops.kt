package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.eco.core.map.nestedListMap
import com.willfp.eco.core.map.nestedMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectReplantCrops : Effect<NoCompileData>("replant_crops") {
    override val arguments = arguments {
        require("consume_seeds", "You must specify if seeds should be consumed!")
        require("only_fully_grown", "You must specify only fully grown crops should be replanted!")
    }

    private val players = nestedMap<UUID, UUID, ReplantConfig>()

    override fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        players[player.uniqueId][identifiers.uuid] = ReplantConfig(
            config.getBool("consume_seeds"),
            config.getBool("only_fully_grown")
        )
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        players[player.uniqueId].remove(identifiers.uuid)
    }

    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: BlockBreakEvent) {
        val player = event.player

        if (players[player.uniqueId].isEmpty()) {
            return
        }

        val block = event.block
        val type = block.type

        if (type in arrayOf(
                Material.GLOW_BERRIES,
                Material.SWEET_BERRY_BUSH,
                Material.CACTUS,
                Material.BAMBOO,
                Material.CHORUS_FLOWER,
                Material.SUGAR_CANE
            )
        ) {
            return
        }

        val data = block.blockData

        if (data !is Ageable) {
            return
        }

        val consumeSeeds = players[player.uniqueId].any { (_, config) ->
            config.consumeSeeds
        }

        val onlyFullyGrown = players[player.uniqueId].all { (_, config) ->
            config.onlyFullyGrown
        }

        if (consumeSeeds) {
            val item = ItemStack(
                when (type) {
                    Material.WHEAT -> Material.WHEAT_SEEDS
                    Material.POTATOES -> Material.POTATO
                    Material.CARROTS -> Material.CARROT
                    Material.BEETROOTS -> Material.BEETROOT_SEEDS
                    else -> type
                }
            )

            val hasSeeds = player.inventory.removeItem(item).isEmpty()

            if (!hasSeeds) {
                return
            }
        }

        if (data.age != data.maximumAge) {
            if (onlyFullyGrown) {
                return
            }

            event.isDropItems = false
            event.expToDrop = 0
        }

        data.age = 0

        plugin.scheduler.run {
            block.type = type
            block.blockData = data

            // Improves compatibility with other plugins.
            Bukkit.getPluginManager().callEvent(
                BlockPlaceEvent(
                    block,
                    block.state,
                    block.getRelative(BlockFace.DOWN),
                    player.inventory.itemInMainHand,
                    player,
                    true,
                    EquipmentSlot.HAND
                )
            )
        }
    }

    private data class ReplantConfig(
        val consumeSeeds: Boolean,
        val onlyFullyGrown: Boolean
    )
}
