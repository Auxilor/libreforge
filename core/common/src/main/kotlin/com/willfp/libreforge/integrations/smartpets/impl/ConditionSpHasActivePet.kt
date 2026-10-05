package com.willfp.libreforge.integrations.smartpets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.smartpets.activePet
import org.bukkit.entity.Player

object ConditionSpHasActivePet : Condition<NoCompileData>("sp_has_active_pet") {
    override val description = "Passes when the player has an active SmartPets pet."
    override val categories = setOf("pets")
    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        return dispatcher.get<Player>()?.activePet != null
    }
}
