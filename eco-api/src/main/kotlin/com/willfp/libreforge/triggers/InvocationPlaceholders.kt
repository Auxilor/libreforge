package com.willfp.libreforge.triggers

import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.events.TriggerCreatePlaceholdersEvent
import com.willfp.libreforge.getHits
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object InvocationPlaceholderListener : Listener {
    @EventHandler
    fun createPlaceholders(event: TriggerCreatePlaceholdersEvent) {
        val data = event.data

        event.addPlaceholder(
            NamedArgument(
                listOf("trigger_value", "triggervalue", "trigger", "value", "tv", "v", "t"),
                event.value
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

                event.addPlaceholder(
                    NamedArgument(
                        "hits",
                        data.victim.getHits(data.player)
                    )
                )
            }
        }
    }
}
