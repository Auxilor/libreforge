package com.willfp.libreforge.effects.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.topInventory
import org.bukkit.block.BrewingStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.roundToInt

object EffectBrewTimeMultiplier : MultiplierEffect("brew_time_multiplier") {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        val multiplier = getMultiplier(player.toDispatcher())

        if (player.topInventory.holder !is BrewingStand) {
            return
        }

        // 2 seconds later to allow for the brewing stand to update, I guess.
        // This is from old EcoSkills code
        plugin.scheduler.runLater(2) {
            val stand = player.topInventory.holder

            if (stand is BrewingStand) {
                if (stand.brewingTime == 400) {
                    stand.brewingTime = (stand.brewingTime * multiplier).roundToInt()
                    stand.update()
                    player.updateInventory()
                }
            }
        }
    }
}
