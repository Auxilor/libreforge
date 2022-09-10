package com.willfp.libreforge.integrations.ecojobs

import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent
import com.willfp.ecojobs.jobs.Job
import com.willfp.libreforge.triggers.WrappedEvent

class WrappedJobXpEvent(
    private val event: PlayerJobExpGainEvent
) : WrappedEvent<PlayerJobExpGainEvent> {
    var amount: Double
        get() = event.amount
        set(value) {
            event.amount = value
        }

    val job: Job
        get() = event.job
}
