package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.ExperienceOrb
import kotlin.random.Random

object EffectDropXp : Effect<NoCompileData>("drop_xp") {
    override val description = "Drops experience orbs at the trigger location."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "xp",
            "You must specify the amount of xp to drop!",
            description = "The amount of experience to drop. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    private val XP_TIERS = intArrayOf(2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1)

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        var remaining = config.getIntFromExpression("xp", data)

        if (remaining <= 0) return false

        val world = location.world ?: return false

        while (remaining > 0) {
            val value = XP_TIERS.first { it <= remaining }
            remaining -= value

            val spawnLoc = location.clone().add(
                Random.nextDouble(-0.5, 0.5),
                Random.nextDouble(0.0, 0.5),
                Random.nextDouble(-0.5, 0.5)
            )

            world.spawn(spawnLoc, ExperienceOrb::class.java) { orb ->
                orb.experience = value
            }
        }

        return true
    }
}