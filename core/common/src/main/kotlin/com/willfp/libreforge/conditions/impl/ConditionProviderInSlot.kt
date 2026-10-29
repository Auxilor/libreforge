package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionProviderInSlot : Condition<List<SlotType>>("provider_in_slot") {
    override val description = "Passes when the item providing the effect is currently in one of the specified slots."

    override val categories = setOf("inventory")

    override val arguments = arguments {
        require(
            listOf("slot", "slots"),
            "You must specify the slot(s)!",
            description = "The slot(s) to check, e.g. hands, armor, mainhand, or a numeric slot index.",
            type = ArgType.STRING_LIST,
            example = listOf("hands", "armor")
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: List<SlotType>
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>()
        val item = holder.getProvider<ItemStack>()

        if (entity == null || item == null) {
            return false
        }

        return compileData.any { item in it.getItems(entity) }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<SlotType> {
        return config.getStrings(pluralPath = "slots", singularPath = "slot")
            .mapNotNull { SlotTypes[it] }
    }
}
