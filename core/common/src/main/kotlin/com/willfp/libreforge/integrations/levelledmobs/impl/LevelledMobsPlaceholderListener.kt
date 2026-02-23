package com.willfp.libreforge.integrations.levelledmobs.impl

import com.willfp.eco.util.namespacedKeyOf
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType

object LevelledMobsPlaceholderListener : Listener {
    private val key = namespacedKeyOf("levelledmobs", "level")

    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val victim = event.trigger.data.victim ?: return
        val level = victim.persistentDataContainer.get(key, PersistentDataType.INTEGER) ?: 0

        event.trigger.addPlaceholder(
            NamedValue(
                "victim_level",
                level
            )
        )
    }
}
