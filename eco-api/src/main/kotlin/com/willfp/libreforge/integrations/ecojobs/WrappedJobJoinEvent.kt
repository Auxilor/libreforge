package com.willfp.libreforge.integrations.ecojobs

import com.willfp.ecojobs.api.event.PlayerJobJoinEvent
import com.willfp.ecojobs.api.event.PlayerJobLevelUpEvent
import com.willfp.ecojobs.jobs.Job
import com.willfp.libreforge.triggers.WrappedEvent

class WrappedJobJoinEvent(
    private val event: PlayerJobJoinEvent
) : WrappedEvent<PlayerJobJoinEvent> {
    val job: Job
        get() = event.job
}
