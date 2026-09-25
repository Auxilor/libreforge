package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

object FilterAboveHunger : Filter<NoCompileData, Double>("above_hunger") {
    override val description = "Matches when the victim's hunger level is at or above the given value."
    override val categories = setOf("entity", "player")
    override val valueType = ArgType.DOUBLE
    override val additionalInfo = listOf("Passes automatically when the victim is missing or is not a player.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Double {
        return config.getDoubleFromExpression(key, data)
    }

    override fun isMet(data: TriggerData, value: Double, compileData: NoCompileData): Boolean {
        val player = data.victim as? Player ?: return true

        return player.foodLevel >= value
    }
}
