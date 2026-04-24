package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.event.entity.EntityRegainHealthEvent

object EffectGiveHealth : Effect<NoCompileData>("give_health") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of health to give!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)!!.value

        if (config.getBoolOrNull("trigger_heal") == true) {
            val missingHealth = maxHealth - player.health

            if (missingHealth <= 0) return false

            val requestedAmount = config.getDoubleFromExpression("amount", data)
            val actualAmount = requestedAmount.coerceAtMost(missingHealth)

            val event = EntityRegainHealthEvent(player, actualAmount, EntityRegainHealthEvent.RegainReason.CUSTOM)
            player.server.pluginManager.callEvent(event)

            if (event.isCancelled) return false

            player.health = (player.health + event.amount).coerceAtMost(maxHealth)
        } else {
            player.health = (player.health + config.getDoubleFromExpression("amount", data)).coerceAtMost(maxHealth)
        }

        return true
    }
}
