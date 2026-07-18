package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.FoodLevelChangeEvent


object ConditionBelowHungerPercent : Condition<NoCompileData>("below_hunger_percent") {
    override val description = "Passes when the player's hunger level is at or below the given percentage."
    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "percent",
            "You must specify the hunger percentage!",
            description = "The maximum hunger percentage (0–100).",
            type = ArgType.EXPRESSION,
            example = "25 + %level% * 0.5"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.foodLevel / 20.0 <= config.getDoubleFromExpression("percent", player) / 100
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        event.entity.toDispatcher().updateEffects()
    }
}
