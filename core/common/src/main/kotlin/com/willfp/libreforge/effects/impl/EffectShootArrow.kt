package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.event.entity.EntityShootBowEvent

object EffectShootArrow : Effect<NoCompileData>("shoot_arrow") {
    override val description = "Shoots an arrow from the player, optionally inheriting bow fire and velocity."
    override val categories = setOf("combat")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        optional(
            "inherit_velocity",
            description = "Whether the arrow should inherit the player's current velocity.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "launch-at-location",
            description = "Whether the arrow should be teleported to the trigger location after launch.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "allow_pickup",
            description = "Whether the arrow can be picked up by players.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "no_source",
            description = "Whether the arrow should have no shooter, preventing attribution to the player.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "damage",
            description = "The base damage of the arrow. If omitted, uses the arrow's default damage.",
            type = ArgType.EXPRESSION,
            default = "2",
            example = "2 + %level%"
        )
        optional(
            "pierce_level",
            description = "The number of entities the arrow can pierce through.",
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

        player.runExempted {
            val arrow = if (velocity == null || !config.getBool("inherit_velocity")) {
                player.launchProjectile(Arrow::class.java)
            } else {
                player.launchProjectile(Arrow::class.java, velocity)
            }

            if (config.getBool("launch-at-location") && data.location != null) {
                arrow.teleportAsync(data.location)
            }

            val pickupStatus = if (config.getBool("allow_pickup")) {
                AbstractArrow.PickupStatus.ALLOWED
            } else {
                AbstractArrow.PickupStatus.DISALLOWED
            }

            arrow.pickupStatus = pickupStatus

            if (damage != null) {
                arrow.damage = damage
            }

            if (pierceLevel != null) {
                arrow.pierceLevel = pierceLevel
            }

            if (fire) {
                arrow.fireTicks = Int.MAX_VALUE
            }

            if (config.getBool("no_source")) {
                arrow.shooter = null
            }
        }

        return true
    }
}
