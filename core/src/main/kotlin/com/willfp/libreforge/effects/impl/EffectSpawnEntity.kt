package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.Listener

class EffectSpawnEntity : Effect(
    "spawn_entity",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
), Listener {
    override val arguments = arguments {
        require("entity", "You must specify the mob to spawn!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return

        val entity = Entities.lookup(config.getString("entity"))
        entity.spawn(location)
    }
}
