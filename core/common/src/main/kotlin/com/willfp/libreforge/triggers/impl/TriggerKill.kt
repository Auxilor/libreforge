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
    override val description = "Fires when the player kills an entity."

    override val categories = setOf("combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was killed.",
        TriggerParameter.LOCATION to "The location of the killed entity.",
        TriggerParameter.ITEM to "The item in the killer's main hand.",
        TriggerParameter.VALUE to "The maximum health of the killed entity."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDeathByEntityEvent) {
        val killer = event.killer.tryAsLivingEntity() ?: return

        val victim = event.victim

        this.dispatch(
            killer.toDispatcher(),
            TriggerData(
                player = killer as? Player,
                victim = victim,
                location = victim.location,
                value = victim.getAttribute(Attribute.MAX_HEALTH)!!.value
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
                value = victim.getAttribute(Attribute.MAX_HEALTH)!!.value
            )
        )
    }
}
