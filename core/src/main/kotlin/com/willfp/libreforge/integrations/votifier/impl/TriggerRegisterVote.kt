package com.willfp.libreforge.integrations.votifier.impl

import com.vexsoftware.votifier.model.VotifierEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerRegisterVote : Trigger("register_vote") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: VotifierEvent) {
        val username = event.vote.username ?: return
        val player = Bukkit.getPlayerExact(username) ?: return
        val service = event.vote.serviceName ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = service
            )
        )
    }
}
