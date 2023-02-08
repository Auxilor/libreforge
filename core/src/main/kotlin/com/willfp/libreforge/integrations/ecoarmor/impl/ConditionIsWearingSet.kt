package com.willfp.libreforge.integrations.ecoarmor.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionIsWearingSet : Condition<NoCompileData>("is_wearing_set") {
    override val arguments = arguments {
        require("set", "You must specify the set name!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return ArmorUtils.getSetOnPlayer(player)?.id == config.getString("set")
    }
}
