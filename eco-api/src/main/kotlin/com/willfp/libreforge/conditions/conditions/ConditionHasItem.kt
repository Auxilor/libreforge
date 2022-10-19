package com.willfp.libreforge.conditions.conditions

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionHasItem : GenericItemCondition("has_item", {
    it.inventory.contents.toList()
})
