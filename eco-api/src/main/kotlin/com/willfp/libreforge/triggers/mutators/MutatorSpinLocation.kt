package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorSpinLocation : DataMutator("spin_location") {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val location = data.location?.clone() ?: return data
        val world = location.world ?: return data
        val vector = location.toVector()

        val distanceVector = location.direction
            .rotateAroundY(Math.toRadians(config.getDoubleFromExpression("angle", data)))
            .setY(0.0)
            .normalize()
            .multiply(config.getDoubleFromExpression("distance", data))

        vector.add(distanceVector)

        return data.copy(
            location = vector.toLocation(world)
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

        if (!config.has("distance")) violations.add(
            ConfigViolation(
                "distance",
                "You must specify the distance from the current location!"
            )
        )

        return violations
    }
}
