package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionWearingHelmet : GenericItemCondition("wearing_helmet", {
    listOf(it.inventory.helmet)
})
