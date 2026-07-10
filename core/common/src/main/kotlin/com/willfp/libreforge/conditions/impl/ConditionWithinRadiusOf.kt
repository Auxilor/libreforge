package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.util.Vector
import kotlin.math.pow

object ConditionWithinRadiusOf : Condition<NoCompileData>("within_radius_of") {
    override val description = "Passes when the entity is within the specified radius of a given coordinate."
    override val categories = setOf("world")

    override val arguments = arguments {
        require(
            "x",
            "You must specify the x coordinate!",
            description = "The X coordinate of the target location.",
            type = ArgType.EXPRESSION
        )
        require(
            "y",
            "You must specify the y coordinate!",
            description = "The Y coordinate of the target location.",
            type = ArgType.EXPRESSION
        )
        require(
            "z",
            "You must specify the z coordinate!",
            description = "The Z coordinate of the target location.",
            type = ArgType.EXPRESSION
        )
        require(
            "radius",
            "You must specify the radius!",
            description = "The maximum distance from the target location.",
            type = ArgType.EXPRESSION,
            example = "15 + %level% * 0.5"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false

        val vector = Vector(
            config.getDoubleFromExpression("x", dispatcher.get()),
            config.getDoubleFromExpression("y", dispatcher.get()),
            config.getDoubleFromExpression("z", dispatcher.get())
        )

        val dist = config.getDoubleFromExpression("radius", dispatcher.get()).pow(2)

        return location.toVector().distanceSquared(vector) <= dist
    }
}
