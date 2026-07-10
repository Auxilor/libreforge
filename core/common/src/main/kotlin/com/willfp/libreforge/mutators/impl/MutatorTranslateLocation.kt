package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorTranslateLocation : Mutator<NoCompileData>("translate_location") {
    override val description = "Offsets the location by the given amounts along each axis."

    override val categories = setOf("location")

    override val arguments = arguments {
        require(
            "add_x",
            "You must specify the value to add to x!",
            description = "The amount to add to the X coordinate.",
            type = ArgType.EXPRESSION,
            example = "0.5"
        )
        require(
            "add_y",
            "You must specify the value to add to y!",
            description = "The amount to add to the Y coordinate.",
            type = ArgType.EXPRESSION,
            example = "1.0"
        )
        require(
            "add_z",
            "You must specify the value to add to z!",
            description = "The amount to add to the Z coordinate.",
            type = ArgType.EXPRESSION,
            example = "-0.5"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location?.clone()?.apply {
            this.x += config.getDoubleFromExpression("add_x", data)
            this.y += config.getDoubleFromExpression("add_y", data)
            this.z += config.getDoubleFromExpression("add_z", data)
        } ?: return data

        return data.copy(location = location)
    }
}