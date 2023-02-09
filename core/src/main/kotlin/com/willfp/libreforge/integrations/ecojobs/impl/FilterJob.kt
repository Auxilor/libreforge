package com.willfp.libreforge.integrations.ecojobs.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecojobs.api.event.JobEvent
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterJob : Filter<NoCompileData, Collection<String>>("job") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? JobEvent ?: return true

        return value.any { jobName ->
            jobName.equals(event.job.id, ignoreCase = true)
        }
    }
}
