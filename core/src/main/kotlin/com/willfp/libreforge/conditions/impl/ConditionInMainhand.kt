package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionInMainhand : GenericItemCondition("in_mainhand", {
    listOf(it.inventory.itemInMainHand)
})
