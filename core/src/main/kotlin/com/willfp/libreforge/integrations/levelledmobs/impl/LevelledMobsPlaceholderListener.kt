package com.willfp.libreforge.integrations.levelledmobs.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import io.github.arcaneplugins.levelledmobs.LevelledMobs
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object LevelledMobsPlaceholderListener : Listener {
    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val victim = event.trigger.data.victim ?: return
        val level = LevelledMobs.instance.levelInterface.getLevelOfMob(victim)

        event.trigger.addPlaceholder(
            NamedValue(
                "victim_level",
                level
            )
        )
    }
}
