package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.EcoPetsAPI
import com.willfp.ecopets.api.event.PlayerPetLevelUpEvent
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class ConditionHasPetLevel : Condition("has_pet_level") {
    override val arguments = arguments {
        require("pet", "You must specify the pet!")
        require("level", "You must specify the level!")
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerPetLevelUpEvent) {
        val player = event.player

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return EcoPetsAPI.instance.getPetLevel(
            player,
            Pets.getByID(config.getString("pet").lowercase()) ?: return false
        ) >= config.getIntFromExpression("level", player)
    }
}
