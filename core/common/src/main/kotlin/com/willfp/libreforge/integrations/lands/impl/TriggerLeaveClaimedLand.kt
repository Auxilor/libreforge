package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.PlayerLeaveLandEvent
import me.angeschossen.lands.api.events.player.area.PlayerAreaEnterEvent
import me.angeschossen.lands.api.events.player.area.PlayerAreaLeaveEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerLeaveClaimedLand : Trigger("leave_claimed_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerAreaLeaveEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location
            )
        )
    }
}