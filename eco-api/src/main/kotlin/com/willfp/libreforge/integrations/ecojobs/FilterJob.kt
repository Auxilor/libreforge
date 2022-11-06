package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

class FilterJob : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val job = (data.event as? WrappedJobXpEvent)?.job
            ?: (data.event as? WrappedJobLevelUpEvent)?.job
            ?: (data.event as? WrappedJobJoinEvent)?.job
            ?: return true

        return config.withInverse("job", Config::getStrings) {
            it.any { jobName ->
                jobName.equals(job.id, ignoreCase = true)
            }
        }
    }
}
