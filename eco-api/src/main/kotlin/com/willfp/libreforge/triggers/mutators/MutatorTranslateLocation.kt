package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorTranslateLocation : DataMutator("translate_location") {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val location = data.location?.clone()?.apply {
            this.x += config.getDoubleFromExpression("add_x", data)
            this.y += config.getDoubleFromExpression("add_y", data)
            this.z += config.getDoubleFromExpression("add_z", data)
        } ?: return data

        return data.copy(location = location)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("add_x")) violations.add(
            ConfigViolation(
                "add_x",
                "You must specify the value to add to x!"
            )
        )

        if (!config.has("add_y")) violations.add(
            ConfigViolation(
                "add_y",
                "You must specify the value to add to y!"
            )
        )

        if (!config.has("add_z")) violations.add(
            ConfigViolation(
                "add_z",
                "You must specify the value to add to z!"
            )
        )

        return violations
    }
}
