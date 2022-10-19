package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.InvocationData

interface EffectArgument {
    fun isPresent(
        config: Config
    ): Boolean

    fun isMet(
        effect: ConfiguredEffect,
        data: InvocationData,
        config: Config
    ): Boolean

    fun ifNotMet(
        effect: ConfiguredEffect,
        data: InvocationData,
        config: Config
    ) {

    }

    fun ifMet(
        effect: ConfiguredEffect,
        data: InvocationData,
        config: Config
    ) {

    }
}
