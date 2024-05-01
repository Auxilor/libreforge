package com.willfp.libreforge.integrations.votifier

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.votifier.impl.FilterVoteService
import com.willfp.libreforge.integrations.votifier.impl.TriggerRegisterVote
import com.willfp.libreforge.triggers.Triggers

object VotifierIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerRegisterVote)
        Filters.register(FilterVoteService)
    }

    override fun getPluginName(): String {
        return "Votifier"
    }
}
