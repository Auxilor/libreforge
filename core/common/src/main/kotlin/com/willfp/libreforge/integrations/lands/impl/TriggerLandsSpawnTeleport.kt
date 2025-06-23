package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.land.spawn.LandSpawnTeleportEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerLandsSpawnTeleport : Trigger("lands_spawn_teleport") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandSpawnTeleportEvent) {
        val landPlayer = event.landPlayer
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                text = event.land.name
            )
        )
    }
}