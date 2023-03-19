package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.core.map.nestedMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.DispatchedTrigger
import java.util.UUID

object ArgumentEvery: EffectArgument<NoCompileData>("every") {
    private val everyHandler = nestedMap<UUID, UUID, Int>()

    override fun isMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val current = everyHandler[element.uuid][trigger.player.uniqueId] ?: 0

        return current == 0
    }

    override fun ifMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        increment(element, trigger)
    }

    override fun ifNotMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        increment(element, trigger)
    }

    private fun increment(element: ElementLike, trigger: DispatchedTrigger) {
        val every = element.config.getIntFromExpression("every", trigger.data)

        var current = everyHandler[element.uuid][trigger.player.uniqueId] ?: 0

        current++

        if (current >= every) {
            current = 0
        }

        everyHandler[element.uuid][trigger.player.uniqueId] = current
    }
}
