package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTameEvent

object TriggerTameAnimal : Trigger("tame_animal") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityTameEvent) {
        val player = event.owner as? Player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = event.entity,
                location = player.location,
                event = event
            )
        )
    }
}
