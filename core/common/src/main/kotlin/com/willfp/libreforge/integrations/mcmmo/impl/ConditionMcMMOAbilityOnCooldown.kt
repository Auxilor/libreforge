package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.datatypes.skills.SuperAbilityType
import com.gmail.nossr50.util.player.UserManager
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionMcMMOAbilityOnCooldown : Condition<NoCompileData>("mcmmo_ability_on_cooldown") {
    override val arguments = arguments {
        require("abilities", "You must specify the ability!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val mcmmoPlayer = UserManager.getPlayer(player) ?: return false

        val abilityNames = config.getStrings("abilities").map { it.uppercase() } ?: return false

        return abilityNames.any { abilityName ->
            val ability = try {
                SuperAbilityType.valueOf(abilityName)
            } catch (e: IllegalArgumentException) {
                return@any false
            }
            mcmmoPlayer.isAbilityOnCooldown(ability)
        }
    }
}

