package com.willfp.libreforge.effects.impl.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float3
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity

abstract class AOEShape<T>(
    override val id: String
) : Compilable<T>() {
    abstract fun getEntities(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: T
    ): Collection<LivingEntity>

    abstract fun getBlocks(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: T
    ): Collection<Block>
}
