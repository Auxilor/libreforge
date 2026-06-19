package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectNameEntity : Effect<NoCompileData>("name_entity") {
    override val description = "Sets the custom name of the victim entity and makes it always visible."
    override val categories = setOf("entity")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "name",
            "You must specify the name to set!",
            description = "The custom name to display above the entity. Supports placeholders.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        victim.isCustomNameVisible = true
        @Suppress("DEPRECATION")
        victim.customName = config.getFormattedString("name", data)

        return true
    }
}
