package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.chains.CycleChainCompileData
import com.willfp.libreforge.chains.EffectChains
import com.willfp.libreforge.chains.NormalChainCompileData
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Triggers

class EffectRunChain : Effect(
    "run_chain",
    applicableTriggers = Triggers.values()
) {
    override fun handle(invocation: InvocationData, config: Config) {
        val chain = EffectChains.getByID(config.getString("chain")) ?: return
        val namedArgs = mutableListOf<NamedArgument>()
        val args = config.getSubsection("chain_args")

        for (key in args.getKeys(false)) {
            namedArgs.add(
                NamedArgument(
                    key,
                    PlaceholderManager.translatePlaceholders(args.getString(key), invocation.player)
                )
            )
        }

        chain(invocation, namedArgs)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("chain")) violations.add(
            ConfigViolation(
                "chain",
                "You must specify the chain to run!"
            )
        )

        return violations
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        return when (config.getString("run-type").lowercase()) {
            "cycle" -> CycleChainCompileData()
            else -> NormalChainCompileData
        }
    }
}
