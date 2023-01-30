package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionWearingChestplate : GenericItemCondition("wearing_chestplate", {
    listOf(it.inventory.chestplate)
})
