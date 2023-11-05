package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent

object FilterFullyCharged : Filter<NoCompileData, Boolean>("fully_charged") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        return when (val event = data.event) {
            is EntityDamageByEntityEvent -> {
                val player = event.damager as? Player ?: return true
                player.attackCooldown >= 1f == value
            }
            is EntityShootBowEvent -> {
                event.force >= 1f == value
            }
            else -> true
        }
    }
}
