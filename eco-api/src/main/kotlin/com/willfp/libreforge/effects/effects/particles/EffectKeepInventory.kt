package com.willfp.libreforge.effects.effects.particles

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.getEffectAmount
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.Triggers.PLAYER_DEATH
import com.willfp.libreforge.triggers.wrappers.WrappedPlayerDeathEvent
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectKeepInventory : Effect(
    "keep_inventory",
    applicableTriggers = listOf(PLAYER_DEATH)
) {

    override fun handle(data: TriggerData, config: Config) {
        val event = data.event?: return
        if (event !is WrappedPlayerDeathEvent) return
        event.keepInventory = config.getBool("keep-inventory")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("keep-inventory")) violations.add(
            ConfigViolation(
                "keep-inventory",
                "You must specify the keep-inventory: true/false parameter!"
            )
        )

        return violations
    }
}