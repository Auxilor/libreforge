package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.GroupedStaticPlaceholder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRunChainInline : Effect<Chain?>("run_chain_inline") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(listOf("effects", "chain"), "You must specify the effects!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: Chain?): Boolean {
        val player = data.player ?: return false

        val dispatch = data.dispatch(player)

        val args = config.getSubsection("chain_args")

        for (key in args.getKeys(false)) {
            dispatch.addPlaceholder(
                GroupedStaticPlaceholder(
                    listOf(key, key.replace("_", "")),
                    PlaceholderManager.translatePlaceholders(args.getString(key), player)
                )
            )
        }

        return compileData?.trigger(dispatch) == true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Chain? {
        return if (config.has("chain")) {
            Effects.compileChain(
                config.getSubsections("chain.effects"),
                ChainExecutors.getByID(config.getString("run-type")) ?: NormalExecutorFactory.create(),
                context.with("run_chain_inline"),
            )
        } else {
            Effects.compileChain(
                config.getSubsections("effects"),
                ChainExecutors.getByID(config.getString("run-type")) ?: NormalExecutorFactory.create(),
                context.with("run_chain_inline"),
            )
        }
    }
}
