package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRunChain : Effect<NoCompileData>("run_chain") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("chain", "You must specify the chain to run!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val dispatch = data.dispatch(EntityDispatcher(player))

        val args = config.getSubsection("chain_args")

        for (key in args.getKeys(false)) {
            dispatch.addPlaceholder(
                NamedValue(
                    listOf(key, key.replace("_", "")),
                    args.getString(key)
                        .formatEco(args.toPlaceholderContext(data))
                )
            )
        }

        val chain = Effects.getChainByID(config.getString("chain")) ?: return false

        return chain.trigger(
            dispatch,
            ChainExecutors.getByID(config.getStringOrNull("run-type"))
        )
    }
}
