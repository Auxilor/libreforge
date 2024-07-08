package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.entities.impl.EmptyTestableEntity
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get

object ConditionNearEntity : Condition<Collection<TestableEntity>>("near_entity") {
    override val arguments = arguments {
        require("entities", "You must specify the list of allowed entities!")
        require("radius", "You must specify the radius!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: Collection<TestableEntity>
    ): Boolean {
        val location = dispatcher.location ?: return false
        val radius = config.getDoubleFromExpression("radius", dispatcher.get())

        // Default to require there to be at least 1 entity if no minimum is defined by the user.
        val nearbyEntityMinimumRequirement = config.getIntFromExpression("amount", dispatcher.get()).coerceAtLeast(1)
        val nearbyEntitiesWhichMatchCriteria = location.world.getNearbyEntities(location, radius, radius, radius).count {
            compileData.any { test -> test.matches(it) }
        }

        return nearbyEntitiesWhichMatchCriteria >= nearbyEntityMinimumRequirement
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableEntity> {
        return config.getStrings("entities").map {
            Entities.lookup(it)
        }.filterNot { it is EmptyTestableEntity }
    }
}
