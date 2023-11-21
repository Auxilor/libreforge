package com.willfp.libreforge.conditions.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

abstract class ItemCondition(id: String) : Condition<Collection<TestableItem>>(id) {
    override val arguments = arguments {
        require(listOf("items", "item"), "You must specify the items!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: Collection<TestableItem>
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false

        return getItems(entity)
            .filterNotEmpty()
            .any { compileData.any { test -> test.matches(it) } }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableItem> {
        return config.getStrings("items", "item")
            .filter { it.isNotBlank() }
            .map { Items.lookup(it) }
            .filterNot { it is EmptyTestableItem }
    }

    abstract fun getItems(entity: LivingEntity): Collection<ItemStack?>
}
