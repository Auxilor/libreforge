package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.balance
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.GenericEffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.InvocationData

object EffectArgumentChance : GenericEffectArgument {
    override fun isPresent(config: Config): Boolean =
        config.has("chance")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        return NumberUtils.randFloat(0.0, 100.0) <= config.getDoubleFromExpression("chance", data.data)
    }
}
