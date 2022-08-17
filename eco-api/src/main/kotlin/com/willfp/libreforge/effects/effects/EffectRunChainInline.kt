package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.chains.ChainCompileData
import com.willfp.libreforge.chains.CycleChainCompileData
import com.willfp.libreforge.chains.EffectChain
import com.willfp.libreforge.chains.EffectChains
import com.willfp.libreforge.chains.NormalChainCompileData
import com.willfp.libreforge.chains.RandomChainCompileData
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Triggers

class EffectRunChainInline : Effect(
    "run_chain_inline",
    triggers = Triggers.all()
) {
    override fun handle(invocation: InvocationData, config: Config) {
        val compileData = invocation.compileData as? InlineEffectChainCompileData ?: return
        val chain = compileData.data
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

        val chainInvocation = invocation.copy(compileData = compileData.child)
        chain(chainInvocation, namedArgs)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("chain") && !config.has("effects")) violations.add(
            ConfigViolation(
                "chain / effects",
                "You must create a chain or specify effects directly!"
            )
        )

        return violations
    }

    override fun makeCompileData(config: Config, context: String): CompileData? {
        val chain = if (config.has("chain")) {
            EffectChains.compile(
                config.getSubsection("chain"),
                "$context -> Inline Chain",
                anonymous = true
            )
        } else {
            EffectChains.compile(
                config,
                "$context -> Inline Chain",
                anonymous = true
            )
        } ?: return null

        return makeCompileData(config, context, chain)
    }

    fun makeCompileData(config: Config, context: String, chain: EffectChain?): CompileData? {
        val invocator = when (config.getString("run-type").lowercase()) {
            "cycle" -> CycleChainCompileData()
            "random" -> RandomChainCompileData()
            else -> NormalChainCompileData
        }

        if (chain == null) {
            return null
        }

        return InlineEffectChainCompileData(chain, invocator)
    }

    private class InlineEffectChainCompileData(
        override val data: EffectChain,
        val child: ChainCompileData
    ) : CompileData
}
