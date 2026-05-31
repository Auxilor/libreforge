package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import java.util.UUID

object EffectDamageNearbyEntities : Effect<Collection<TestableEntity>>("damage_nearby_entities") {
    override val description = "Deals damage to all nearby entities within a radius."
    override val categories = setOf("combat")

    private val damagedEntities = mutableSetOf<UUID>()

    override val parameters = setOf(
        TriggerParameter.LOCATION, TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius to damage entities within. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        require(
            "damage_as_player",
            "You must specify if the player should be marked as the damager!",
            description = "Whether the player is attributed as the source of damage.",
            type = ArgType.BOOLEAN
        )
        require(
            "damage",
            "You must specify the damage to deal!",
            description = "The amount of damage to deal to each entity. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "entities",
            description = "If specified, only these entity types will be damaged.",
            type = ArgType.ENTITY_LIST
        )
        optional(
            "damage_self",
            description = "Whether the player can damage themselves with this effect.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
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
            if (entity.hasMetadata("ignore-nearby-damage") || damagedEntities.contains(entity.uniqueId)) {
                continue
            }

            if (entity !is LivingEntity) {
                continue
            }

            if (!AntigriefManager.canInjure(player, entity)) {
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

            damagedEntities.add(entity.uniqueId)

            if (damageAsPlayer) {
                entity.damage(damage, player)
            } else {
                entity.damage(damage)
            }
        }

        damagedEntities.clear()

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableEntity> {
        return config.getStrings("entities").map { Entities.lookup(it) }
    }
}
