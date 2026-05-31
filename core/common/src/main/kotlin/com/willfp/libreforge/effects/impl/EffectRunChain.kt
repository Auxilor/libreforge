package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData

object EffectRunChain : Effect<NoCompileData>("run_chain") {
    override val description = "Runs a named chain of effects defined in `plugins/libreforge/chains.yml`."
    override val categories = setOf("meta")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "chain",
            "You must specify the chain to run!",
            description = "The ID of the chain to execute.",
            type = ArgType.STRING
        )
        optional(
            "chain_args",
            description = "A subsection of key-value pairs to expose as placeholders within the chain.",
            type = ArgType.ANY
        )
        optional(
            "run-type",
            description = "The chain executor type to use when running the chain.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val dispatch = data.dispatch(data.dispatcher)

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
