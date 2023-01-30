package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionWearingLeggings : GenericItemCondition("wearing_leggings", {
    listOf(it.inventory.leggings)
})
