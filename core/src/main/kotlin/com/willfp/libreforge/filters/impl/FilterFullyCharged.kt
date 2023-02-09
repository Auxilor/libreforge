package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

object FilterFullyCharged : Filter<NoCompileData, Boolean>("fully_charged") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageByEntityEvent ?: return true
        val player = event.damager as? Player ?: return true
        return player.attackCooldown >= 1f == value
    }
}
