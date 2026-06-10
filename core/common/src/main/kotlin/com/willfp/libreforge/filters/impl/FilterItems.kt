package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.matches
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.EditableDropEvent

object FilterItems : Filter<Collection<TestableItem>, Collection<String>>("items") {
    override val description = "Matches when the held item matches one of the given item types."
    override val categories = setOf("inventory")
    override val valueType = ArgType.ITEM_LIST
    override val additionalInfo = listOf("Passes automatically when no items are present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: Collection<TestableItem>): Boolean {
        val itemList = listOf(data.item)

        val unfilteredItems = (data.event as? EditableDropEvent)?.items?.map { it.item } ?: itemList

        val items = unfilteredItems.filterNotNull()

        if (items.isEmpty()) {
            return true
        }

        return compileData.matches(items)
    }

    override fun makeCompileData(
        config: Config, context: ViolationContext, values: Collection<String>
    ): Collection<TestableItem> {
        return values.map { Items.lookup(it) }
    }
}
