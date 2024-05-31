package com.willfp.libreforge.integrations.levelledmobs.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType

object LevelledMobsPlaceholderListener : Listener {
    private var key: NamespacedKey? = null

    fun load(){
        val levelledMobsPlugin = Bukkit.getPluginManager().getPlugin("LevelledMobs")
        key = NamespacedKey(levelledMobsPlugin!!, "level")
    }

    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val victim = event.trigger.data.victim ?: return
        val level = victim.persistentDataContainer.get(key!!, PersistentDataType.INTEGER) ?: 0

        event.trigger.addPlaceholder(
            NamedValue(
                "victim_level",
                level
            )
        )
    }
}
