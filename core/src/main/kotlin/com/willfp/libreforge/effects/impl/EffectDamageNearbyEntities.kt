package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object EffectDamageNearbyEntities : Effect<Collection<TestableEntity>>("damage_nearby_entities") {
    override val parameters = setOf(
        TriggerParameter.LOCATION, TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
        require("damage_as_player", "You must specify if the player should be marked as the damager!")
        require("damage", "You must specify the damage to deal!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: Collection<TestableEntity>): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false
        val player = data.player ?: return false

        val radius = config.getDoubleFromExpression("radius", data)
        val damageAsPlayer = config.getBool("damage_as_player")
        val damage = config.getDoubleFromExpression("damage", data)
        val damageSelf = config.getBoolOrNull("damage_self") ?: true

        for (entity in world.getNearbyEntities(location, radius, radius, radius)) {
            if (entity.hasMetadata("ignore-nearby-damage")) {
                continue
            }

            if (entity !is LivingEntity) {
                continue
            }

            if (compileData.isNotEmpty()) {
                if (compileData.none { it.matches(entity) }) {
                    continue
                }
            }

            entity.setMetadata("ignore-nearby-damage", plugin.metadataValueFactory.create(true))
            plugin.scheduler.runLater(5) { entity.removeMetadata("ignore-nearby-damage", plugin) }

            if (!damageSelf && (entity == player)) {
                continue
            }

            if (damageAsPlayer) {
                entity.damage(damage, player)
            } else {
                entity.damage(damage)
            }
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableEntity> {
        return config.getStrings("entities").map { Entities.lookup(it) }
    }
}
