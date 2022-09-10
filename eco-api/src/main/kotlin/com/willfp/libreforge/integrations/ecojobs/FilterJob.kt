package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterJob : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val job = (data.event as? WrappedJobXpEvent)?.job
            ?: (data.event as? WrappedJobLevelUpEvent)?.job
            ?: return true

        return config.withInverse("job", Config::getStringsOrNull) {
            it?.any { jobName ->
                jobName.equals(job.id, ignoreCase = true)
            } == true
        }
    }
}
