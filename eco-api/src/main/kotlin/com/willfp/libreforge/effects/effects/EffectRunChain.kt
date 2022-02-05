package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.chains.EffectChains
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Triggers

class EffectRunChain : Effect(
    "run_chain",
    supportsFilters = true,
    applicableTriggers = Triggers.values()
) {
    override fun handle(invocation: InvocationData, config: Config) {
        val chain = EffectChains.getByID(config.getString("chain")) ?: return
        chain(invocation)
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
}