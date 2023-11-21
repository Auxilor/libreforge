package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent

private fun Material.isDrink(): Boolean {
    return  this == Material.BEETROOT_SOUP ||
            this == Material.MUSHROOM_STEW ||
            this == Material.SUSPICIOUS_STEW ||
            this == Material.RABBIT_STEW ||
            this == Material.POTION ||
            this == Material.HONEY_BOTTLE ||
            this == Material.GLASS_BOTTLE
}

object TriggerDrink : Trigger("drink") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemConsumeEvent) {
        if (!event.item.type.isDrink()) {
            return
        }
        val player = event.player
        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                event = event,
                item = event.item
            )
        )
    }
}
