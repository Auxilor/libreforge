package com.willfp.libreforge.integrations.smartpets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.smartpets.activePet
import com.willfp.libreforge.integrations.smartpets.smartPets
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSpUnlockSkill : Effect<NoCompileData>("sp_unlock_skill") {
    override val description = "Unlocks a skill for the player's active SmartPets pet."
    override val categories = setOf("pets")
    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "skill",
            "You must specify the skill!",
            description = "The ID of the skill to unlock.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val pet = player.activePet ?: return false

        return smartPets?.petUnlockSkill(player.uniqueId, pet, config.getString("skill").lowercase()) ?: false
    }
}
