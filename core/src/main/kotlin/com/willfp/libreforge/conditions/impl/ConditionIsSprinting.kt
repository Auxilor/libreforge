package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.triggers.hasCondition
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerToggleSprintEvent

object ConditionIsSprinting : Condition<NoCompileData>("is_sprinting") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.isSprinting
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PlayerToggleSprintEvent) {
        val player = event.player.toDispatcher()

        if (!player.hasCondition(this)) {
            return
        }

        player.updateEffects()
    }
}
