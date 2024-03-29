package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
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

    private val players = listMap<UUID, ReplantConfig>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        players[dispatcher.uuid] += ReplantConfig(
            identifiers.uuid,
            config.getBool("consume_seeds"),
            config.getBool("only_fully_grown")
        )
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        players[dispatcher.uuid].removeIf { it.uuid == identifiers.uuid }
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

        if (!AntigriefManager.canPlaceBlock(player, block)) {
            return
        }

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

        val consumeSeeds = players[player.uniqueId].any {
            it.consumeSeeds
        }

        val onlyFullyGrown = players[player.uniqueId].all {
            it.onlyFullyGrown
        }

        if (consumeSeeds) {
            val item = ItemStack(
                when (type) {
                    Material.WHEAT -> Material.WHEAT_SEEDS
                    Material.POTATOES -> Material.POTATO
                    Material.CARROTS -> Material.CARROT
                    Material.BEETROOTS -> Material.BEETROOT_SEEDS
                    Material.COCOA -> Material.COCOA_BEANS
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
        val uuid: UUID,
        val consumeSeeds: Boolean,
        val onlyFullyGrown: Boolean
    )
}
