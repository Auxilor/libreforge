package com.willfp.libreforge.integrations.rosestacker.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import dev.rosewood.rosestacker.api.RoseStackerAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import java.util.UUID

object RoseStackerPlaceholderListener : Listener {
    // Long enough to outlive RoseStacker's asynchronous stack loot calculation.
    private const val CACHE_TICKS = 100L

    // RoseStacker unstacks the entity while handling its death, so the stack size has to be
    // remembered beforehand for triggers like kill and entity_death to be able to read it.
    private val stackSizes = mutableMapOf<UUID, Int>()

    @EventHandler(priority = EventPriority.LOWEST)
    fun cacheStackSize(event: EntityDeathEvent) {
        val stacked = RoseStackerAPI.getInstance().getStackedEntity(event.entity) ?: return
        val uuid = event.entity.uniqueId

        stackSizes[uuid] = stacked.stackSize
        plugin.scheduler.runLater(CACHE_TICKS) { stackSizes -= uuid }
    }

    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val victim = event.trigger.data.victim ?: return

        val stackSize = stackSizes[victim.uniqueId]
            ?: RoseStackerAPI.getInstance().getStackedEntity(victim)?.stackSize
            ?: 1

        event.trigger.addPlaceholder(
            NamedValue(
                "victim_stack_size",
                stackSize
            )
        )
    }
}
