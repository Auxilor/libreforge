package com.willfp.libreforge.integrations.ecoitems.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecoitems.items.ItemUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionHasEcoItem : Condition<NoCompileData>("has_ecoitem") {
    override val arguments = arguments {
        require("item", "You must specify the item!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return ItemUtils.getEcoItemsOnPlayer(player).map { it.id }
            .containsIgnoreCase(config.getString("item"))
    }
}
