package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.InvocationData

object EffectArgumentRequire : EffectArgument {
    override fun isPresent(config: Config): Boolean =
        config.has("require")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        return config.getDoubleFromExpression("require", data.data) == 1.0
    }
}
