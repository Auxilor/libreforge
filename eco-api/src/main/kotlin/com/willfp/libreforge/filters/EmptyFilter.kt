package com.willfp.libreforge.filters

import com.willfp.libreforge.triggers.TriggerData

class EmptyFilter: Filter {
    override fun matches(data: TriggerData): Boolean {
        return true
    }
}
