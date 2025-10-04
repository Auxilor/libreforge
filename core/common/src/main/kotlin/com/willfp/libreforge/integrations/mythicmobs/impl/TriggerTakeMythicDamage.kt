package com.willfp.libreforge.integrations.mythicmobs.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerTakeMythicDamage : Trigger("take_mythic_damage") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: MythicDamageEvent) {
        val caster = event.caster?.entity?.bukkitEntity?.tryAsLivingEntity() ?: return
        val victim = event.target?.bukkitEntity ?: return

        this.dispatch(
            victim.toDispatcher(),
            TriggerData(
                player = victim as? Player,
                victim = caster,
                location = caster.location,
                event = event,
                value = event.damage
            )
        )
    }
}