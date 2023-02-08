package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorSpinLocation : Mutator<NoCompileData>("spin_location") {
    override val arguments = arguments {
        require("angle", "You must specify the angle to rotate by!")
        require("distance", "You must specify the distance from the current location!")
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
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
}
