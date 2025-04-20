package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.evaluateExpression
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData

object FilterIsExpressionTrue : Filter<Config, String>("is_expression_true") {
    override fun getValue(config: Config, data: TriggerData?, key: String): String {
        return config.getString(key)
    }

    override fun isMet(data: TriggerData, value: String, compileData: Config): Boolean {
        return evaluateExpression(value, compileData.toPlaceholderContext(data)) > 0
    }

    override fun makeCompileData(config: Config, context: ViolationContext, values: String): Config {
        return config
    }
}
