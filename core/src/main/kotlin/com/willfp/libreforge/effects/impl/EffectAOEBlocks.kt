package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.impl.aoe.AOECompileData
import com.willfp.libreforge.effects.impl.aoe.AOEShapes
import com.willfp.libreforge.get
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.LivingEntity

object EffectAOEBlocks : Effect<AOECompileData>("aoe_blocks") {
    override val isPermanent = false

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("shape", "You must specify a valid shape!", Config::getString) {
            AOEShapes[it] != null
        }
        inherit { AOEShapes[it.getString("shape")] }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: AOECompileData): Boolean {
        val location = data.location?.clone()
            ?: data.dispatcher.location?.clone()
            ?: return false

        val dispatcherLocation = data.dispatcher.location

        if (dispatcherLocation != null) {
            if (location.world == dispatcherLocation.world
                && location.distanceSquared(dispatcherLocation) <= 1.0
            ) {
                location.add(0.0, data.dispatcher.get<LivingEntity>()?.eyeHeight ?: 0.0, 0.0)
                location.direction = dispatcherLocation.direction
            }
        }

        val shape = compileData.shape ?: return false

        for (block in shape.getBlocks(
            location.toFloat3(),
            location.direction.toFloat3(),
            location.world,
            data
        ).filterNot { it.isEmpty }) {
            compileData.chain
                ?.trigger(
                    data.copy(
                        block = block,
                        location = block.location.add(0.5, 0.5, 0.5)
                    ).dispatch(data.dispatcher)
                )
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): AOECompileData {
        return AOECompileData(
            AOEShapes.compile(config, context),
            Effects.compileRichChain(
                config,
                context.with("aoe effects")
            )
        )
    }
}
