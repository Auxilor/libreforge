package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.LivingEntity


class EffectDamageNearbyEntities : Effect(
    "damage_nearby_entities",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.LOCATION,
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val player = data.player ?: return

        val radius = config.getDoubleFromExpression("radius", player)
        val damageAsPlayer = config.getBool("damage_as_player")
        val entities = config.getStrings("entities").map { Entities.lookup(it) }
        val damage = config.getDoubleFromExpression("damage", player)
        val damageSelf = config.getBoolOrNull("damage_self") ?: true

        for (entity in location.getNearbyEntities(radius, radius, radius)) {
            if (entity.hasMetadata("ignore-nearby-damage")) {
                continue
            }

            if (entity !is LivingEntity) {
                continue
            }

            if (entities.none { it.matches(entity) }) {
                continue
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
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius!"
            )
        )

        if (!config.has("damage_as_player")) violations.add(
            ConfigViolation(
                "damage_as_player",
                "You must specify if the player should be marked as the damager!"
            )
        )

        if (!config.has("entities")) violations.add(
            ConfigViolation(
                "entities",
                "You must specify the list of valid entites!"
            )
        )

        if (!config.has("damage")) violations.add(
            ConfigViolation(
                "damage",
                "You must specify the damage to deal!"
            )
        )

        return violations
    }
}