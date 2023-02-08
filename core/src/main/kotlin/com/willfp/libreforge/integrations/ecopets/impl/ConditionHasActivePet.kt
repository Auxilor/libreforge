package com.willfp.libreforge.integrations.ecopets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.EcoPetsAPI
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionHasActivePet : Condition<NoCompileData>("has_active_pet") {
    override val arguments = arguments {
        require("pet", "You must specify the pet!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return EcoPetsAPI.instance.hasPet(
            player,
            Pets.getByID(config.getString("pet").lowercase()) ?: return false
        )
    }
}
