package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData

object FilterValueBelow : Filter<NoCompileData, Double>("value_below") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Double {
        return config.getDoubleFromExpression(key, data)
    }

    override fun isMet(data: TriggerData, value: Double, compileData: NoCompileData): Boolean {
        return data.value < value
    }
}
