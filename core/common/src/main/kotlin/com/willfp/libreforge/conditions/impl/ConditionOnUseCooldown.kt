package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.useCooldownItem
import com.willfp.libreforge.useCooldownSeconds
import org.bukkit.entity.Player

object ConditionOnUseCooldown : Condition<NoCompileData>("on_use_cooldown") {
    override val description = "Passes when the player has a use cooldown running, from the use_cooldown item component."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "With percent set to true, the full cooldown length is read from the use_cooldown component of the held item, or of an item in the inventory with the same group. If no such item is found, the condition does not pass."
    )

    override val arguments = arguments {
        optional(
            "group",
            description = "The cooldown group to check, as namespace:id. Defaults to the cooldown of the held item.",
            type = ArgType.STRING,
            example = "myserver:dash"
        )
        optional(
            "above",
            description = "Only passes when the time left is at or above this value. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "2"
        )
        optional(
            "below",
            description = "Only passes when the time left is at or below this value. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "5"
        )
        optional(
            "percent",
            description = "If above and below are a percentage of the full cooldown instead of seconds.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val group = config.getStringOrNull("group")

        val secondsLeft = player.useCooldownItem(group)?.let { player.getCooldown(it) / 20.0 } ?: 0.0

        val value = if (config.getBool("percent")) {
            player.useCooldownSeconds(group)?.takeIf { it > 0 }?.let { secondsLeft / it * 100 }
        } else {
            secondsLeft
        }

        return secondsLeft > 0 && value != null
                && (!config.has("above") || value >= config.getDoubleFromExpression("above", player))
                && (!config.has("below") || value <= config.getDoubleFromExpression("below", player))
    }
}
