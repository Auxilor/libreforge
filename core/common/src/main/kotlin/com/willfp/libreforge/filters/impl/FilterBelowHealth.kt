package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData

object FilterBelowHealth : Filter<NoCompileData, Double>("below_health") {
    override val description = "Matches when the victim's current health is at or below the given value."
    override val categories = setOf("entity", "combat")
    override val valueType = ArgType.DOUBLE
    override val additionalInfo = listOf("Passes automatically when no victim is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Double {
        return config.getDoubleFromExpression(key, data)
    }

    override fun isMet(data: TriggerData, value: Double, compileData: NoCompileData): Boolean {
        val entity = data.victim ?: return true

        return entity.health <= value
    }
}
