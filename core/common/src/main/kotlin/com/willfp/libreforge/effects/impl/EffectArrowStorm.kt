package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
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
import org.bukkit.util.Vector

object EffectArrowStorm : Effect<NoCompileData>("arrow_storm") {
    override val description = "Rains arrows down from above the trigger location."
    override val categories = setOf("combat")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the number of arrows!",
            description = "The number of arrows to rain down. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        require(
            "height",
            "You must specify the spawn height above target!",
            description = "The height above the target to spawn the arrows. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        require(
            "spread",
            "You must specify the spread radius!",
            description = "The horizontal spread radius for arrow spawning. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "damage",
            description = "The damage each arrow deals. If omitted, uses the arrow's default damage.",
            type = ArgType.EXPRESSION
        )
        optional(
            "respect_flame",
            description = "Whether arrows inherit the Flame enchantment from the held bow.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false
        val amount = config.getIntFromExpression("amount", data)
        val height = config.getDoubleFromExpression("height", data)
        val spread = config.getDoubleFromExpression("spread", data)
        val damage = config.getOrNull("damage") { getDoubleFromExpression(it, data) }
        val respectFlame = config.getBoolOrNull("respect_flame") ?: true
        val flame = respectFlame && (data.item?.containsEnchantment(org.bukkit.enchantments.Enchantment.FLAME) ?: false)

        repeat(amount) {
            val spawnLoc = location.clone().add(
                NumberUtils.randFloat(-spread, spread),
                height,
                NumberUtils.randFloat(-spread, spread)
            )
            val arrow = world.spawn(spawnLoc, Arrow::class.java)
            arrow.velocity = Vector(0.0, -2.0, 0.0)
            arrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
            if (damage != null) arrow.damage = damage
            if (flame) arrow.setFireTicks(Int.MAX_VALUE)
        }

        return true
    }
}
