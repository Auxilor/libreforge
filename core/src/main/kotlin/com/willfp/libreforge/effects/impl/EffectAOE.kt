package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.impl.aoe.AOEBlock
import com.willfp.libreforge.effects.impl.aoe.AOEShapes
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectAOE : Effect<EffectAOE.AOEData>("aoe") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("shape", "You must specify a valid shape!", Config::getString) {
            AOEShapes.getByID(it) != null
        }
        inherit { AOEShapes.getByID(it.getString("shape")) }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: AOEData): Boolean {
        val player = data.player ?: return false
        val shape = compileData.shape ?: return false

        for (entity in shape.getEntities(
            player.location.toFloat3(),
            player.eyeLocation.direction.toFloat3(),
            player.location.world,
            data
        ).filter { it != player }) {
            compileData.chain?.trigger(data.dispatch(player))
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): AOEData {
        return AOEData(
            AOEShapes.compile(config, context),
            Effects.compileChain(
                config.getSubsections("effects"),
                NormalExecutorFactory.create(),
                context.with("aoe Effects")
            )
        )
    }

    data class AOEData(
        val shape: AOEBlock<*>?,
        val chain: Chain?
    )
}
