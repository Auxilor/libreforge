package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.events.PlayerJumpEvent
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerJump : Trigger("jump") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJumpEvent) {
        val player = event.player

        val runnable = Runnable {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    location = player.location,
                    event = event,
                    velocity = player.velocity
                )
            )
        }

        if (Prerequisite.HAS_FOLIA.isMet) {
            if (player.isValid) // folia issue, sometimes player moves when is dead, teleporting, etc, making the task impossible
                plugin.scheduler.runTask(player, runnable)
        } else
            runnable.run()
    }
}
