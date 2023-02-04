package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
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
    override val arguments = arguments {
        require(listOf("effects", "chain"), "You must specify the effects!")
    }

    override fun handle(invocation: InvocationData, config: Config) {
        val compileData = invocation.compileData as? InlineEffectChainCompileData ?: return
        val chain = compileData.chain
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

        val chainInvocation = invocation.copy(compileData = compileData.child)
        chain(chainInvocation, namedArgs)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): CompileData? {
        val chain = if (config.has("chain")) {
            EffectChains.compile(
                config.getSubsection("chain"),
                context.with("Inline Chain"),
                anonymous = true
            )
        } else {
            EffectChains.compile(
                config,
                context.with("Inline Chain"),
                anonymous = true
            )
        } ?: return null

        return makeCompileData(config, chain)
    }

    fun makeCompileData(config: Config, chain: EffectChain?): CompileData? {
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
        val chain: EffectChain,
        val child: ChainCompileData
    ) : CompileData
}
