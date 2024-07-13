package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.hasCondition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.FoodLevelChangeEvent


object ConditionAboveHungerPercent : Condition<NoCompileData>("above_hunger_percent") {
    override val arguments = arguments {
        require("percent", "You must specify the hunger percentage!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.foodLevel / 20 >= config.getDoubleFromExpression("percent", player) / 100
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        val entity = event.entity.toDispatcher()

        if (!entity.hasCondition(this)) return

        entity.updateEffects()
    }
}
