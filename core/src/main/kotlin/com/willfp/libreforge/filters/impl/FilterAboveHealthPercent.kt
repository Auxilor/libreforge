package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.attribute.Attribute

object FilterAboveHealthPercent : Filter<NoCompileData, Double>("above_health_percent") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Double {
        return config.getDoubleFromExpression(key, data)
    }

    override fun isMet(data: TriggerData, value: Double, compileData: NoCompileData): Boolean {
        val entity = data.victim ?: return true
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
        val percent = (entity.health / maxHealth) * 100

        return percent >= value
    }
}
