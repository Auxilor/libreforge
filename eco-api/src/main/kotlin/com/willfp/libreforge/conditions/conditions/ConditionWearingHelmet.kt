package com.willfp.libreforge.conditions.conditions

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionWearingHelmet : GenericItemCondition("wearing_helmet", {
    listOf(it.inventory.helmet)
})
