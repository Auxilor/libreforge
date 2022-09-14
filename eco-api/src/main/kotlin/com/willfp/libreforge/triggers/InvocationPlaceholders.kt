package com.willfp.libreforge.triggers

import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.events.TriggerCreatePlaceholdersEvent
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import sun.jvm.hotspot.oops.CellTypeState.value

object InvocationPlaceholderListener : Listener {
    @EventHandler
    fun createPlaceholders(event: TriggerCreatePlaceholdersEvent) {
        val data = event.data

        event.addPlaceholder(
            NamedArgument(
                listOf("trigger_value", "triggervalue", "trigger", "value", "tv", "v", "t"),
                value.toString()
            )
        )

        if (data.victim != null) {
            event.addPlaceholder(
                NamedArgument(
                    "victim_health",
                    data.victim.health
                )
            )

            event.addPlaceholder(
                NamedArgument(
                    "victim_max_health",
                    data.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
                )
            )

            if (data.player != null) {
                event.addPlaceholder(
                    NamedArgument(
                        "distance",
                        data.victim.location.toVector().distance(data.player.location.toVector())
                    )
                )
            }
        }
    }
}
