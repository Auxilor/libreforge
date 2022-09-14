package com.willfp.libreforge.integrations.levelledmobs

import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.events.TriggerCreatePlaceholdersEvent
import me.lokka30.levelledmobs.LevelledMobs
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object LevelledMobsPlaceholderListener : Listener {
    @EventHandler
    fun createPlaceholders(event: TriggerCreatePlaceholdersEvent) {
        val victim = event.data.victim ?: return
        val level = LevelledMobs.getInstance().levelInterface.getLevelOfMob(victim)

        event.addPlaceholder(
            NamedArgument(
                "victim_level",
                level
            )
        )
    }
}
