package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.balance
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.GenericEffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.InvocationData

object EffectArgumentCost: GenericEffectArgument {
    override fun isPresent(config: Config): Boolean =
        config.has("cost")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        val player = data.player
        return player.balance >= config.getDoubleFromExpression("cost", data.data)
    }

    override fun ifNotMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        effect.effect.sendCannotAffordMessage(data.player, config.getDoubleFromExpression("cost", data.data))
    }

    override fun ifMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        data.player.balance -= config.getDoubleFromExpression("cost", data.data)
    }
}
