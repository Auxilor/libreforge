package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.FoodLevelChangeEvent

object ConditionAboveHunger : Condition<NoCompileData>("above_hunger") {
    override val description = "Passes when the player's hunger level is at or above the given amount."
    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "hunger",
            "You must specify the hunger level!",
            description = "The minimum hunger level (0-20).",
            type = ArgType.EXPRESSION,
            example = "10 + %level%"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.foodLevel >= config.getDoubleFromExpression("hunger", player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        event.entity.toDispatcher().updateEffects()
    }
}
