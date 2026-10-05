package com.willfp.libreforge.integrations.smartpets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.smartpets.activePet
import org.bukkit.entity.Player

object ConditionSpHasSkill : Condition<NoCompileData>("sp_has_skill") {
    override val description = "Passes when the player's active SmartPets pet has the specified skill unlocked."
    override val categories = setOf("pets")
    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val arguments = arguments {
        require(
            "skill",
            "You must specify the skill!",
            description = "The ID of the skill.",
            type = ArgType.STRING
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val pet = dispatcher.get<Player>()?.activePet ?: return false

        return pet.hasSkill(config.getString("skill").lowercase())
    }
}
