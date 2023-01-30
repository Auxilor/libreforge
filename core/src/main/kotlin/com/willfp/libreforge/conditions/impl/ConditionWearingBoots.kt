package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionWearingBoots : GenericItemCondition("wearing_boots", {
    listOf(it.inventory.boots)
})
