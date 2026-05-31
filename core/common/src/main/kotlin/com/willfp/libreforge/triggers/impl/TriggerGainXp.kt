package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.DropQueuePushEvent
import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import java.util.UUID

object TriggerGainXp : Trigger("gain_xp") {
    override val description = "Fires when the player gains experience points."

    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    private val telekinesisGranted = mutableSetOf<UUID>()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        val player = event.expChangeEvent.player

        if (player.uniqueId in telekinesisGranted) {
            return
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event.expChangeEvent,
                value = event.expChangeEvent.amount.toDouble()
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: DropQueuePushEvent) {
        if (!event.isTelekinetic) return
        if (event.xp <= 0) return

        val player = event.player

        telekinesisGranted += player.uniqueId
        plugin.scheduler.run {
            telekinesisGranted -= player.uniqueId
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                value = event.xp.toDouble()
            )
        )
    }
}
