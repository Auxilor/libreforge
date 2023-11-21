package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit

object EffectAllPlayers : Effect<Chain?>("all_players") {
    override val isPermanent = false

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: Chain?): Boolean {
        for (player in Bukkit.getOnlinePlayers()) {
            compileData?.trigger(
                data.dispatch(player.toDispatcher()),
            )
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Chain? {
        return Effects.compileChain(
            config.getSubsections("effects"),
            ChainExecutors.getByID(config.getStringOrNull("run-type")),
            context.with("all_players effects")
        )
    }
}
