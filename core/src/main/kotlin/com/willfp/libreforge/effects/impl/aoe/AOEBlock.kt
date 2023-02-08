package com.willfp.libreforge.effects.impl.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float3
import org.bukkit.World
import org.bukkit.entity.LivingEntity

/**
 * A single animation config block.
 */
class AOEBlock<T>(
    val shape: AOEShape<T>,
    override val config: Config,
    override val compileData: T
) : Compiled<T> {
    fun getEntities(
        location: Float3,
        direction: Float3,
        world: World,
        data: TriggerData
    ): Collection<LivingEntity> {
        return shape.getEntities(location, direction, world, config, data, compileData)
    }
}
