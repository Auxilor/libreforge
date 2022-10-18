package com.willfp.libreforge.conditions.conditions

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionWearingChestplate : GenericItemCondition(
    "wearing_chestplate",
    { it.inventory.chestplate }
)
