package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object MutatorProjectileShooterAsPlayer : Mutator<NoCompileData>("projectile_shooter_as_player") {
    override val description = "Sets the player to whoever shot the projectile."

    override val categories = setOf("player", "entity")

    override val additionalInfo = listOf("Sets player to null if the projectile was not shot by a player.")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PROJECTILE becomes TriggerParameter.PLAYER
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            player = data.projectile?.shooter as? Player
        )
    }
}
