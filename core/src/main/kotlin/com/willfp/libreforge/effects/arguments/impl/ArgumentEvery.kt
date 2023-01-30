package com.willfp.libreforge.effects.arguments.impl

import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.DispatchedTrigger
import java.util.UUID

object ArgumentEvery: EffectArgument<NoCompileData>("every") {
    private val everyHandler = mutableMapOf<UUID, MutableMap<UUID, Int>>()

    override fun isMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val current = everyHandler.getOrPut(element.uuid) { mutableMapOf() }[trigger.player.uniqueId] ?: 0

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

        var current = everyHandler.getOrPut(element.uuid) { mutableMapOf() }[trigger.player.uniqueId] ?: 0

        current++

        if (current >= every) {
            current = 0
        }

        everyHandler.getOrPut(element.uuid) { mutableMapOf() }[trigger.player.uniqueId] = current
    }
}
