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
import com.willfp.libreforge.integrations.smartpets.activePet
import org.bukkit.entity.Player

object ConditionSpActivePetType : Condition<NoCompileData>("sp_active_pet_type") {
    override val description = "Passes when the player's active SmartPets pet is one of the specified types."
    override val categories = setOf("pets")
    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val arguments = arguments {
        require(
            "types",
            "You must specify the pet types!",
            description = "The pet types to check for.",
            type = ArgType.STRING_LIST,
            example = "[\"wolf\", \"cat\"]"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val pet = dispatcher.get<Player>()?.activePet ?: return false

        return config.getStrings("types").containsIgnoreCase(pet.type)
    }
}
