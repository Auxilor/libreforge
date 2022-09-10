package com.willfp.libreforge.integrations.ecojobs

import com.willfp.ecojobs.api.event.PlayerJobLevelUpEvent
import com.willfp.ecojobs.jobs.Job
import com.willfp.libreforge.triggers.WrappedEvent

class WrappedJobLevelUpEvent(
    private val event: PlayerJobLevelUpEvent
) : WrappedEvent<PlayerJobLevelUpEvent> {
    val job: Job
        get() = event.job
}
