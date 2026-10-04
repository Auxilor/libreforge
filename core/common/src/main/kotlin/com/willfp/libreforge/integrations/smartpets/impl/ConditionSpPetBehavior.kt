package com.willfp.libreforge.integrations.smartpets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.smartpets.smartPets
import org.bukkit.entity.Player

object ConditionSpPetBehavior : Condition<NoCompileData>("sp_pet_behavior") {
    override val description = "Passes when the player's active SmartPets pet is doing one of the specified behaviors."
    override val categories = setOf("pets")
    override val additionalInfo = listOf(
        "Requires SmartPets Pro to be installed.",
        "Behaviors: ALERT, ASSIST, ATTACK, FLEE, FOLLOW, GUARD, HUNT, MIMIC, PATROL, PERCH, PLAY, REST, SCOUT, SEARCH_FOOD, NONE.",
        "NONE means the pet is idle or not spawned."
    )

    override val arguments = arguments {
        require(
            "behaviors",
            "You must specify the behaviors!",
            description = "The behaviors to check for.",
            type = ArgType.STRING_LIST,
            example = "[\"guard\", \"hunt\"]"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val behavior = smartPets?.getActivePetBehavior(player.uniqueId) ?: return false

        return config.getStrings("behaviors").containsIgnoreCase(behavior)
    }
}
