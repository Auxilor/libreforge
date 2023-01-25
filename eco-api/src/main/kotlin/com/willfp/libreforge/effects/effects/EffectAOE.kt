package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.effects.aoe.AOEShapes
import com.willfp.libreforge.effects.effects.particles.toVector3f
import com.willfp.libreforge.effects.invoke
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class EffectAOE : Effect(
    "aoe",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("shape", "You must specify a valid shape!", Config::getString) {
            AOEShapes.getByID(it) != null
        }
        inherit { AOEShapes.getByID(it.getString("shape")) }
    }

    override fun handle(invocation: InvocationData, config: Config) {
        val player = invocation.data.player ?: return
        val effects = invocation.compileData as? AOEEffects ?: return

        val shape = AOEShapes.getByID(config.getString("shape")) ?: return

        for (entity in shape.getEntities(
            player.location.toVector3f(),
            player.eyeLocation.direction.toVector3f(),
            player.location.world,
            config,
            invocation.data
        ).filter { it != player }) {
            effects(player, entity)
        }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): CompileData {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("aoe Effects"),
            chainLike = true
        )

        return AOEEffects(
            effects
        )
    }

    private data class AOEEffects(
        val effects: List<ConfiguredEffect>
    ) : CompileData {
        operator fun invoke(player: Player, victim: LivingEntity) {
            effects(
                player,
                TriggerData(
                    player = player,
                    victim = victim,
                    location = victim.location,
                    block = victim.location.clone().subtract(0.0, 1.0, 0.0).block
                ),
                useTriggerPlayerForConditions = true
            )
        }
    }
}
