package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionHasItem : GenericItemCondition("has_item", {
    it.inventory.contents.toList()
})
