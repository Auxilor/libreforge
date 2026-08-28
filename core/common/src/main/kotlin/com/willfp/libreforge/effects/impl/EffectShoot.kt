package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityShootBowEvent

@Suppress("UNCHECKED_CAST")
object EffectShoot : Effect<NoCompileData>("shoot") {
    override val description = "Launches a projectile of the specified entity type from the player."
    override val categories = setOf("combat")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "projectile",
            "You must specify the projectile!",
            description = "The entity type to launch as a projectile, e.g. ARROW or SNOWBALL.",
            type = ArgType.ENTITY,
            enumClass = EntityType::class
        )
        optional(
            "inherit_velocity",
            description = "Whether the projectile should inherit the player's current velocity.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "launch-at-location",
            description = "Whether the projectile should be teleported to the trigger location after launch.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "allow_pickup",
            description = "Whether the projectile can be picked up by players (applies to arrows).",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "no_source",
            description = "Whether the projectile should have no shooter, preventing attribution to the player.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "damage",
            description = "The base damage of the projectile (applies to arrows). " +
                    "If omitted, uses the arrow's default damage.",
            type = ArgType.EXPRESSION,
            default = "2",
            example = "2 + %level%"
        )
        optional(
            "pierce_level",
            description = "The number of entities the projectile can pierce through (applies to arrows).",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "1 + %level% / 20"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val velocity = data.velocity
        val fire = ((data.event as? EntityShootBowEvent)?.projectile?.fireTicks ?: 0) > 0
        val damage = config.getOrNull("damage") { getDoubleFromExpression(it, data) }
        val pierceLevel = config.getOrNull("pierce_level") { getIntFromExpression(it, data) }

        val projectileClass = enumValueOfOrNull<EntityType>(config.getString("projectile").uppercase())?.entityClass
            ?: return false

        player.runExempted {
            val projectile = if (velocity == null || !config.getBool("inherit_velocity")) {
                player.launchProjectile(projectileClass as Class<out Projectile>)
            } else {
                player.launchProjectile(projectileClass as Class<out Projectile>, velocity)
            }

            if (config.getBool("launch-at-location") && data.location != null) {
                projectile.teleportAsync(data.location)
            }

            if (projectile is AbstractArrow) {
                val pickupStatus = if (config.getBool("allow_pickup")) {
                    AbstractArrow.PickupStatus.ALLOWED
                } else {
                    AbstractArrow.PickupStatus.DISALLOWED
                }

                projectile.pickupStatus = pickupStatus

                if (damage != null) {
                    projectile.damage = damage
                }

                if (pierceLevel != null) {
                    projectile.pierceLevel = pierceLevel
                }
            }

            if (fire) {
                projectile.fireTicks = Int.MAX_VALUE
            }

            if (config.getBool("no_source")) {
                projectile.shooter = null
            }
        }

        return true
    }
}
