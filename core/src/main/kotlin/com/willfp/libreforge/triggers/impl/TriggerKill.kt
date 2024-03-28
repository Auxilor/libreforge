package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerKill : Trigger("kill") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDeathByEntityEvent) {
        if (!isEnabled) return
        val killer = event.killer.tryAsLivingEntity() ?: return

        val victim = event.victim

        this.dispatch(
            killer.toDispatcher(),
            TriggerData(
                player = killer as? Player,
                victim = victim,
                location = victim.location,
                value = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            )
        )
    }

    fun force(player: Player, victim: LivingEntity) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = victim,
                location = victim.location,
                value = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            )
        )
    }
}
