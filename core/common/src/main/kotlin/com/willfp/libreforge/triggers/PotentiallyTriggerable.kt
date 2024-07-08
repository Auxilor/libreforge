package com.willfp.libreforge.triggers

interface PotentiallyTriggerable {
    /**
     * Check if this can be triggered by some [trigger].
     */
    fun canBeTriggeredBy(trigger: Trigger): Boolean
}
