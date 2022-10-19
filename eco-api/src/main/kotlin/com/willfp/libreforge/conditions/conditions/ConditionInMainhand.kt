package com.willfp.libreforge.conditions.conditions

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionInMainhand : GenericItemCondition("in_mainhand", {
    listOf(it.inventory.itemInMainHand)
})
