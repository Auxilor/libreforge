package com.willfp.libreforge.integrations.rosestacker.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.impl.TriggerEntityDeath
import com.willfp.libreforge.triggers.impl.TriggerKill
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

/**
 * When an entire stack is killed at once, only the entity at the top of the stack dies through
 * EntityDeathEvent, so the triggers for the rest of the stack have to be fired manually.
 */
object RoseStackerStackedDeathListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun handle(event: EntityStackMultipleDeathEvent) {
        if (!plugin.configYml.getBool("integrations.rosestacker.trigger-per-killed-entity")) {
            return
        }

        val extraDeaths = event.entityKillCount - 1

        if (extraDeaths <= 0) {
            return
        }

        val victim = event.mainEntity
        val killer = event.killer

        // RoseStacker calls this event asynchronously when death-event-trigger-async is enabled.
        plugin.scheduler.run {
            repeat(extraDeaths) {
                // The entity at the top of the stack has already been dispatched for by the
                // EntityDeathEvent, so these would otherwise be filtered out as duplicates.
                TriggerEntityDeath.force(victim, allowDuplicates = true)
                killer?.let { TriggerKill.force(it, victim, allowDuplicates = true) }
            }
        }
    }
}
