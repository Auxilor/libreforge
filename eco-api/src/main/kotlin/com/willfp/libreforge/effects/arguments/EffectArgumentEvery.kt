package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.balance
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.GenericEffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.InvocationData
import java.util.UUID

object EffectArgumentEvery: GenericEffectArgument {
    private val everyHandler = mutableMapOf<UUID, MutableMap<UUID, Int>>()

    override fun isPresent(config: Config): Boolean =
        config.has("every")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        val current = everyHandler.getOrPut(effect.uuid) { mutableMapOf() }[data.player.uniqueId] ?: 0

        return current == 0
    }

    override fun always(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        val every = config.getIntFromExpression("every", data.data)

        var current = everyHandler.getOrPut(effect.uuid) { mutableMapOf() }[data.player.uniqueId] ?: 0

        current++

        if (current >= every) {
            current = 0
        }

        everyHandler.getOrPut(effect.uuid) { mutableMapOf() }[data.player.uniqueId] = current
    }
}
