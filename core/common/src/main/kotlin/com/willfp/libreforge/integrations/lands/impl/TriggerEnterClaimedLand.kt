package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.player.area.PlayerAreaEnterEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerEnterClaimedLand : Trigger("enter_claimed_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerAreaEnterEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location

        Bukkit.getScheduler().runTask(plugin, Runnable { 
        // TriggerDispatchEvent may only be triggered synchronously.
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    event = event,
                    location = location,
                    text = event.area.name
                )
            )
        })
    }
}