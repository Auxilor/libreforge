package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerBlank : Trigger("blank") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )
}
