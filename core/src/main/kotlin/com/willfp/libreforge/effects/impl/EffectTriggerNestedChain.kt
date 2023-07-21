package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

/*

Replacement for run_chain_inline, however it is never registered and exists purely
as an internal component. It's used to make nested chains work.

 */

object EffectTriggerNestedChain : Effect<Chain?>("trigger_nested_chain") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: Chain?): Boolean {
        val player = data.player ?: return false

        val dispatch = data.dispatch(player)
        return compileData?.trigger(dispatch) == true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Chain? {
        /*

        trigger_nested_chain is compiled differently to other effects: rather
        than args being passed in as the config, the config is the element itself.

        Therefore, args need to be accessed from the parent config.

         */

        return Effects.compileChain(
            config.getSubsections("effects"),
            ChainExecutors.getByID(config.getStringOrNull("args.run-type")),
            context.with("nested chain"),
        )
    }
}
