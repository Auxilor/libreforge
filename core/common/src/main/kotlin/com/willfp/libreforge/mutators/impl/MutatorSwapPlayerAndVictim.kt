package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object MutatorSwapPlayerAndVictim : Mutator<NoCompileData>("swap_player_and_victim") {
    override val description = "Swaps the player and the victim around."

    override val categories = setOf("player", "victim")

    override val additionalInfo = listOf("Sets player to null if the victim is not a Player.")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.VICTIM
        TriggerParameter.VICTIM becomes TriggerParameter.PLAYER
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            player = data.victim as? Player,
            victim = data.player
        )
    }
}
