package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.useCooldownItem
import kotlin.math.roundToInt

object EffectSetUseCooldown : Effect<NoCompileData>("set_use_cooldown") {
    override val description = "Starts, sets, extends or reduces the player's use cooldown, from the use_cooldown item component."
    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            name = "seconds",
            message = "You must specify the seconds!",
            description = "The cooldown time in seconds. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "2 * %level%"
        )
        optional(
            "group",
            description = "The cooldown group to change, as namespace:id. Defaults to the cooldown of the held item.",
            type = ArgType.STRING,
            example = "myserver:dash"
        )
        optional(
            "mode",
            description = "Set replaces the time left, add extends it and reduce shortens it.",
            type = ArgType.STRING,
            default = "set",
            choices = listOf("set", "add", "reduce")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val ticks = (config.getDoubleFromExpression("seconds", data) * 20).roundToInt().coerceAtLeast(0)

        val item = player.useCooldownItem(config.getStringOrNull("group"))

        item?.let { cooldownItem ->
            player.setCooldown(
                cooldownItem,
                when (config.getStringOrNull("mode")?.lowercase()) {
                    "add" -> player.getCooldown(cooldownItem) + ticks
                    "reduce" -> (player.getCooldown(cooldownItem) - ticks).coerceAtLeast(0)
                    else -> ticks
                }
            )
        }

        return item != null
    }
}
