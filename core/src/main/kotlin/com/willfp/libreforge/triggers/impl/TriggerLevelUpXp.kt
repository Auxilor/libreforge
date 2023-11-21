package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerLevelChangeEvent

object TriggerLevelUpXp : Trigger("level_up_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerLevelChangeEvent) {
        val player = event.player

        if (event.newLevel < event.oldLevel) {
            return
        }

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                value = event.newLevel.toDouble()
            )
        )
    }
}
