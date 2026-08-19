package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData

object MutatorPlayerAsDispatcher : Mutator<NoCompileData>("player_as_dispatcher") {
    override val description = "Sets the dispatcher to the current player."

    override val categories = setOf("player", "meta")

    override val additionalInfo = listOf("No-ops if the trigger data has no player.")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            dispatcher = data.player?.toDispatcher() ?: return data
        )
    }
}
