package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Mob
import org.bukkit.event.Listener

class EffectSpawnEntity : Effect(
    "spawn_entity",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
), Listener {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return

        val entity = Entities.lookup(config.getString("entity"))
        entity.spawn(location)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("entity")) violations.add(
            ConfigViolation(
                "entity",
                "You must specify the mob to spawn!"
            )
        )

        return violations
    }
}
