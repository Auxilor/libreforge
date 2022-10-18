package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
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
    override fun handle(invocation: InvocationData, config: Config) {
        val player = invocation.data.player ?: return
        val effects = invocation.compileData?.data as? AOEEffects ?: return

        val shape = AOEShapes.getByID(config.getString("shape")) ?: return

        for (entity in shape.getEntities(
            player.location.toVector3f(),
            player.eyeLocation.direction.toVector3f(),
            player.location.world,
            config,
            invocation.data
        )) {
            effects(player, entity)
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("effects")) violations.add(
            ConfigViolation(
                "effects",
                "You must specify the effects!"
            )
        )

        if (!config.has("shape")) violations.add(
            ConfigViolation(
                "shape",
                "You must specify the shape of the AOE area!"
            )
        )

        val shape = AOEShapes.getByID(config.getString("shape"))

        if (shape == null) {
            violations.add(
                ConfigViolation(
                    "shape",
                    "Invalid shape!"
                )
            )
        } else {
            violations.addAll(shape.validateConfig(config))
        }

        return violations
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        val effects = config.getSubsections("effects").mapNotNull {
            Effects.compile(it, "$context -> aoe Effects")
        }.toSet()

        return AOECompileData(
            AOEEffects(
                effects
            )
        )
    }

    private class AOECompileData(
        override val data: AOEEffects
    ) : CompileData

    private data class AOEEffects(
        val effects: Set<ConfiguredEffect>
    ) {
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
