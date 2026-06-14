package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

object EffectSilkTouch : Effect<NoCompileData>("silk_touch") {
    override val description = "Replaces a broken block's drops with the block itself, as if mined with Silk Touch."
    override val categories = setOf("world", "inventory")
    override val additionalInfo = listOf("Requires the block_item_drop trigger")

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EditableDropEvent ?: return false

        if (event.cause != DropCause.BLOCK) {
            return false
        }

        val blockState = event.context.blockState ?: return false

        val drops = if (blockState.type.isItem) {
            val item = ItemStack(blockState.type)
            val meta = item.itemMeta
            if (meta is BlockStateMeta) {
                meta.blockState = blockState
                item.itemMeta = meta
            }
            listOf(item)
        } else {
            val silkTouch = getEnchantment("silk_touch") ?: return false

            val tool = (event.tool ?: ItemStack(Material.AIR)).clone()
            if (tool.type == Material.AIR) {
                tool.type = Material.DIAMOND_PICKAXE
            }

            val meta = tool.itemMeta
            meta.addEnchant(silkTouch, 1, true)
            tool.itemMeta = meta

            val player = event.player
            if (player != null) blockState.getDrops(tool, player) else blockState.getDrops(tool)
        }

        event.drops.clear()
        event.drops.addAll(drops)

        return true
    }
}
