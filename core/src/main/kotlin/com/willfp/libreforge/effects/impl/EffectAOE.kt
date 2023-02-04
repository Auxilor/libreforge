package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.effects.aoe.AOEShapes
import com.willfp.libreforge.effects.effects.particles.toVector3f
import com.willfp.libreforge.effects.invoke
import com.willfp.libreforge.effects.triggerers.impl.NormalTriggererFactory
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectAOE : Effect<Chain?>("aoe") {
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

    override fun onTrigger(config: Config, data: TriggerData, compileData: Chain?): Boolean {
        val player = data.player ?: return false
        val shape = AOEShapes.getByKD(config.getString("shape")) ?: return false

        for (entity in shape.getEntities(
            player.location.toVector3f(),
            player.eyeLocation.direction.toVector3f(),
            player.location.world,
            config,
            invocation.data
        ).filter { it != player }) {
            compileData?.trigger(data.dispatch(player))
        }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Chain? {
        return Effects.compileChain(
            config.getSubsections("effects"),
            NormalTriggererFactory.create(),
            context.with("aoe Effects")
        )
    }
}
