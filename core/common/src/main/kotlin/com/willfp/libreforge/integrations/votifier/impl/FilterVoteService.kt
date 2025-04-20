package com.willfp.libreforge.integrations.votifier.impl

import com.vexsoftware.votifier.model.VotifierEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterVoteService : Filter<NoCompileData, Collection<String>>("vote_service") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? VotifierEvent ?: return true
        val service = event.vote.serviceName ?: return true

        return value.contains(service)
    }
}
