package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.chains.CycleChainCompileData
import com.willfp.libreforge.chains.EffectChains
import com.willfp.libreforge.chains.NormalChainCompileData
import com.willfp.libreforge.chains.RandomChainCompileData
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Triggers

class EffectRunChain : Effect(
    "run_chain",
    triggers = Triggers.all()
) {
    override val arguments = arguments {
        require("chain", "You must specify the chain to run!")
    }

    override fun handle(invocation: InvocationData, config: Config) {
        val chain = EffectChains.getByID(config.getString("chain")) ?: return
        val namedArgs = mutableListOf<NamedArgument>()
        val args = config.getSubsection("chain_args")

        for (key in args.getKeys(false)) {
            namedArgs.add(
                NamedArgument(
                    listOf(key, key.replace("_", "")),
                    PlaceholderManager.translatePlaceholders(args.getString(key), invocation.player)
                )
            )
        }

        chain(invocation, namedArgs)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): CompileData {
        return when (config.getString("run-type").lowercase()) {
            "cycle" -> CycleChainCompileData()
            "random" -> RandomChainCompileData()
            else -> NormalChainCompileData
        }
    }
}
