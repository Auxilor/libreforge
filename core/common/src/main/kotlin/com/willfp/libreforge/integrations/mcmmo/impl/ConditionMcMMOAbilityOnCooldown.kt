package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.datatypes.skills.SuperAbilityType
import com.gmail.nossr50.util.player.UserManager
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionMcMMOAbilityOnCooldown : Condition<NoCompileData>("mcmmo_ability_on_cooldown") {
    override val description = "Passes when any of the specified mcMMO super abilities are currently on cooldown for the player."
    override val categories = setOf("player")
    override val additionalInfo = listOf("Requires the mcMMO plugin.")

    override val arguments = arguments {
        require(
            "abilities",
            "You must specify the ability!",
            description = "A list of mcMMO super ability names to check (e.g. SUPER_BREAKER, GIGA_DRILL_BREAKER).",
            type = ArgType.STRING_LIST
        )
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

