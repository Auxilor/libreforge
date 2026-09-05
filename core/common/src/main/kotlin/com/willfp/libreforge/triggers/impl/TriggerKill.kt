package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import com.willfp.libreforge.triggers.tryAsLivingEntity
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

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
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    // Resolved from the entity's own death state (rather than a snapshot taken
    // at a specific EntityDamageEvent priority) so that it correctly reflects
    // damage multiplier effects, which are applied at EventPriority.MONITOR.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDeathEvent) {
        val victim = event.entity

        val killer = victim.killer
            ?: (victim.lastDamageCause as? EntityDamageByEntityEvent)
                ?.takeUnless { it.isCancelled }
                ?.damager?.tryAsLivingEntity()
            ?: return

        // The death's drops are exposed as an editable drop event so that drop
        // effects (drop_item with add_to_drops, multiply_drops, ...) can edit
        // the entity's own drop list rather than dropping alongside it.
        val dropEvent = EditableDropEvent(
            initialDrops = event.drops.filterNotEmpty(),
            cause = DropCause.ENTITY,
            context = DropContext(
                player = killer as? Player,
                entity = victim
            ),
            dropLocation = victim.location
        )

        this.dispatch(
            killer.toDispatcher(),
            TriggerData(
                player = killer as? Player,
                victim = victim,
                location = victim.location,
                event = dropEvent,
                value = victim.getAttribute(Attribute.MAX_HEALTH)!!.value
            )
        )

        // Read once: reading applies the accumulated modifiers in place, so a
        // second read would apply them twice.
        val results = dropEvent.items

        event.drops.clear()
        event.drops.addAll(dropEvent.drops)

        val xp = results.sumOf { it.xp }

        if (xp > 0) {
            event.droppedExp += xp
        }
    }

    fun force(player: Player, victim: LivingEntity, allowDuplicates: Boolean = false) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = victim,
                location = victim.location,
                value = victim.getAttribute(Attribute.MAX_HEALTH)!!.value
            ),
            allowDuplicates = allowDuplicates
        )
    }
}
