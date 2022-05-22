package com.willfp.libreforge.triggers.triggers

import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedPlayerDeathEvent
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent

class TriggerPlayerDeath : Trigger(
    "player_death", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {

    @EventHandler
    fun handle(event: PlayerDeathEvent) {
        val player = event.player
        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = event.player.location,
                event = WrappedPlayerDeathEvent(event)
            )
        )
    }
}
