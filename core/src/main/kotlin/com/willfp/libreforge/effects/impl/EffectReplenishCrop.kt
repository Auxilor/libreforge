package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object EffectReplenishCrop : MineBlockEffect<NoCompileData>("replenish_crop") {

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("consume-seeds", "You must specify the status of 'consume-seeds'!")
        require("only-fully-grown", "You must specify the status of 'only-fully-grown'!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val consumeSeeds = config.getBoolOrNull("consume-seeds") ?: return false
        val onlyFullyGrown = config.getBoolOrNull("only-fully-grown") ?: return false
        val crop = data.block ?: data.location?.block ?: return false
        val type = crop.type
        val state = crop.blockData as? Ageable ?: return false
        if (type in arrayOf(
                Material.GLOW_BERRIES, Material.SWEET_BERRY_BUSH, Material.CACTUS,
                Material.BAMBOO, Material.CHORUS_FLOWER, Material.SUGAR_CANE
        )) {
            return false
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
                return false
            }
        }
        if (state.age != state.maximumAge) {
            if (onlyFullyGrown) {
                return false
            }
        }
        state.age = 0
        plugin.scheduler.run {
            data.block?.type = type
            data.block?.blockData = state

            // Improves compatibility with other plugins.
            Bukkit.getPluginManager().callEvent(
                BlockPlaceEvent(
                    data.block!!,
                    data.block.state,
                    data.block.getRelative(BlockFace.DOWN),
                    player.inventory.itemInMainHand,
                    player,
                    true,
                    EquipmentSlot.HAND
                )
            )
        }
        return true
    }

}