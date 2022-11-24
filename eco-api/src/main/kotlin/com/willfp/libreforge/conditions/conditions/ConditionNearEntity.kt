package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.entities.impl.EmptyTestableEntity
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player

class ConditionNearEntity : Condition("near_entity") {
    override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        val entities = (data as? EntitiesCompileData)?.entities ?: return true

        val radius = config.getDoubleFromExpression("radius", player)

        return player.getNearbyEntities(radius, radius, radius).any {
            entities.any { test -> test.matches(it) }
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("entities")) violations.add(
            ConfigViolation(
                "entities",
                "You must specify the list of allowed entities!"
            )
        )

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius!"
            )
        )

        return violations
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        return EntitiesCompileData(
            config.getStrings("entities").map {
                Entities.lookup(it)
            }.filterNot { it is EmptyTestableEntity }
        )
    }

    private data class EntitiesCompileData(
        val entities: Collection<TestableEntity>
    ): CompileData
}
