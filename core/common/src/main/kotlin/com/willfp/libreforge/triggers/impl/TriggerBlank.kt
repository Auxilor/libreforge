package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

/**
 * Used to create triggers that don't represent anything,
 * for example turning TriggerData into a DispatchedTrigger.
 */
object TriggerBlank : Trigger("blank") {
    override var isEnabled = true

    override val parameters = TriggerParameter.entries.toSet()
}
