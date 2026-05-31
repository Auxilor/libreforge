package com.willfp.libreforge.integrations.mythicmobs.impl.trigger

import com.willfp.libreforge.integrations.mythicmobs.utils.isMythicMob
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object TriggerTakeMythicDamage : Trigger("take_mythic_damage") {
    override val description = "Fires when a player or entity takes damage from a MythicMobs mob."

    override val categories = setOf("combat")

    override val additionalInfo = listOf(
        "Requires MythicMobs to be installed."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The MythicMobs entity that dealt the damage.",
        TriggerParameter.LOCATION to "The attacker's location.",
        TriggerParameter.VALUE to "The damage dealt."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    // Handler for non-vanilla damage, custom projectiles or lightning 'triggered' by MythicMobs.
    @EventHandler(ignoreCancelled = true)
    fun handleNonVanillaDamage(event: MythicDamageEvent) {
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

    // Handler for vanilla damage, swords and bows etc. (by MythicMobs).
    @EventHandler(ignoreCancelled = true)
    fun handleVanillaDamage(event: EntityDamageByEntityEvent) {
        val caster = event.damager.tryAsLivingEntity() ?: return
        if (!caster.isMythicMob()) return
        val victim = event.entity
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