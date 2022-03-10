package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

class MutatorSpinVelocity : DataMutator("spin_velocity") {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val velocity = data.velocity?.clone() ?: return data

        val newVelocity = velocity
            .rotateAroundY(Math.toRadians(config.getDoubleFromExpression("angle", data.player)))

        return data.copy(
            velocity = newVelocity
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("angle")) violations.add(
            ConfigViolation(
                "angle",
                "You must specify the angle to rotate by!"
            )
        )

        return violations
    }
}
