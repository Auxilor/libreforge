package com.willfp.libreforge.integrations.rosestacker.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import dev.rosewood.rosestacker.api.RoseStackerAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object RoseStackerPlaceholderListener : Listener {
    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val victim = event.trigger.data.victim ?: return
        val stackSize = RoseStackerAPI.getInstance().getStackedEntity(victim)?.stackSize ?: 1

        event.trigger.addPlaceholder(
            NamedValue(
                "victim_stack_size",
                stackSize
            )
        )
    }
}
