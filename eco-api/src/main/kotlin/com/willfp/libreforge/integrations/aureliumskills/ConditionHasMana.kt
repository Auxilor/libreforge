package com.willfp.libreforge.integrations.aureliumskills

import com.archyx.aureliumskills.api.AureliumAPI
import com.archyx.aureliumskills.api.event.ManaRegenerateEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class ConditionHasMana : Condition("has_mana") {
    override val arguments = arguments {
        require("amount", "You must specify the amount of mana!")
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: ManaRegenerateEvent) {
        val player = event.player

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return AureliumAPI.getMana(player) > config.getDoubleFromExpression("amount", player)
    }
}
