package com.willfp.libreforge.filters

import com.willfp.libreforge.triggers.TriggerData

interface Filter {
    fun matches(data: TriggerData): Boolean
}
