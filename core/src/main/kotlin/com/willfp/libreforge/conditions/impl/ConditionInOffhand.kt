package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionInOffhand : GenericItemCondition("in_offhand", {
    listOf(it.inventory.itemInOffHand)
})
