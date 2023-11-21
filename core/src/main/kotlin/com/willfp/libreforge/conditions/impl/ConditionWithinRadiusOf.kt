package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.util.Vector
import kotlin.math.pow

object ConditionWithinRadiusOf : Condition<NoCompileData>("within_radius_of") {
    override val arguments = arguments {
        require("x", "You must specify the x coordinate!")
        require("y", "You must specify the y coordinate!")
        require("z", "You must specify the z coordinate!")
        require("radius", "You must specify the radius!")
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
