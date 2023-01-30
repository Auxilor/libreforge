package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.DispatchedTrigger

object ArgumentChance : EffectArgument<NoCompileData>("chance") {
    override fun isMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        return NumberUtils.randFloat(0.0, 100.0) <= element.config.getDoubleFromExpression("chance", trigger.data)
    }
}
