package com.willfp.libreforge.effects.impl.animations

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * A single animation config block.
 */
class AnimationBlock<T, T2>(
    val animation: Animation<T, T2>,
    override val config: Config,
    override val compileData: T
) : Compiled<T> {
    fun setUp(
        sourceLocation: Location,
        direction: Float3,
        data: TriggerData
    ) = animation.setUp(sourceLocation, direction, config, data, compileData)

    fun play(
        tick: Int,
        sourceLocation: Location,
        direction: Float3,
        triggerData: TriggerData,
        data: T2
    ) = animation.play(tick, sourceLocation, direction, config, triggerData, compileData, data)

    fun finish(
        sourceLocation: Location,
        direction: Float3,
        triggerData: TriggerData,
        data: T2
    ) = animation.finish(sourceLocation, direction, config, triggerData, compileData, data)
}
