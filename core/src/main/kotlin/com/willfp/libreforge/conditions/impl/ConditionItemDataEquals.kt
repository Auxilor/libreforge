package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.itemData
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ConditionItemDataEquals : Condition<NoCompileData>("item_data_equals") {
    override val arguments = arguments {
        require("key", "You must specify the data key!")
        require("value", "You must specify the data value to equal!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val item = holder.getProvider<ItemStack>() ?: return false
        val key = config.getString("key")
        val value = config.getFormattedString("value", placeholderContext(player = dispatcher.get<Player>()))

        return item.itemData[key] == value
    }
}
