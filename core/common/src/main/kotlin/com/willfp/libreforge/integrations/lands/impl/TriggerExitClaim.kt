package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.player.area.PlayerAreaLeaveEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerExitClaim : Trigger("exit_claim") {
    override val description = "Fires when the player exits a Lands claim area."

    override val categories = setOf("movement")

    override val additionalInfo = listOf("Requires Lands to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.TEXT to "The name of the land area exited."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerAreaLeaveEvent) {
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