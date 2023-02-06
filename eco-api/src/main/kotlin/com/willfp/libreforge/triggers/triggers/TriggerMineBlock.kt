package com.willfp.libreforge.triggers.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedBlockBreakEvent
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import java.util.concurrent.TimeUnit

class TriggerMineBlock : Trigger(
    "mine_block", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (block.isMineBlockTriggerPrevented) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                block = block,
                location = block.location,
                event = WrappedBlockBreakEvent(event),
                item = player.inventory.itemInMainHand
            )
        )
    }

    companion object {
        private val prevented = Caffeine.newBuilder()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build<Location, Boolean>()

        fun Block.preventMineBlockTrigger() {
            prevented.put(this.location, true)
        }

        val Block.isMineBlockTriggerPrevented: Boolean
            get() = prevented.getIfPresent(this.location) == true
    }
}
