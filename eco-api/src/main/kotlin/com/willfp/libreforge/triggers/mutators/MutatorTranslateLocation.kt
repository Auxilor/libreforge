package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

class MutatorTranslateLocation : DataMutator("translate_location") {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val player = data.player

        val location = data.location?.clone()?.apply {
            this.x += config.getDoubleFromExpression("add_x", player)
            this.x *= config.getDoubleFromExpression("multiply_x", player)
            this.y += config.getDoubleFromExpression("add_y", player)
            this.y *= config.getDoubleFromExpression("multiply_y", player)
            this.z += config.getDoubleFromExpression("add_z", player)
            this.z *= config.getDoubleFromExpression("multiply_z", player)
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

        if (!config.has("multiply_x")) violations.add(
            ConfigViolation(
                "multiply_x",
                "You must specify the value to multiply x by!"
            )
        )

        if (!config.has("add_y")) violations.add(
            ConfigViolation(
                "add_y",
                "You must specify the value to add to y!"
            )
        )

        if (!config.has("multiply_y")) violations.add(
            ConfigViolation(
                "multiply_y",
                "You must specify the value to multiply y by!"
            )
        )

        if (!config.has("add_z")) violations.add(
            ConfigViolation(
                "add_z",
                "You must specify the value to add to z!"
            )
        )

        if (!config.has("multiply_z")) violations.add(
            ConfigViolation(
                "multiply_z",
                "You must specify the value to multiply z by!"
            )
        )

        return violations
    }
}
