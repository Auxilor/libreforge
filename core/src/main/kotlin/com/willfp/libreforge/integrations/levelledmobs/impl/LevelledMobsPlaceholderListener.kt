package com.willfp.libreforge.integrations.levelledmobs.impl

import com.willfp.libreforge.GroupedStaticPlaceholder
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import me.lokka30.levelledmobs.LevelledMobs
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object LevelledMobsPlaceholderListener : Listener {
    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val victim = event.trigger.data.victim ?: return
        val level = LevelledMobs.getInstance().levelInterface.getLevelOfMob(victim)

        event.trigger.addPlaceholder(
            GroupedStaticPlaceholder(
                "victim_level",
                level
            )
        )
    }
}
