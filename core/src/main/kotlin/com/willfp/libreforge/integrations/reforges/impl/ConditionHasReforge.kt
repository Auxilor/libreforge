package com.willfp.libreforge.integrations.reforges.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.reforges.util.ReforgeLookup
import org.bukkit.entity.Player

object ConditionHasReforge : Condition<NoCompileData>("has_reforge") {
    override val arguments = arguments {
        require("reforge", "You must specify the reforge!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return ReforgeLookup.provideReforges(player).map { it.id }.containsIgnoreCase(config.getString("reforge"))
    }
}
