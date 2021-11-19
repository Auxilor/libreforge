package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.Triggers

class EffectStrikeLightning : Effect(
    "strike_lightning",
    supportsFilters = false,
    applicableTriggers = listOf(
        Triggers.ALT_CLICK,
        Triggers.MELEE_ATTACK,
        Triggers.BOW_ATTACK,
        Triggers.TRIDENT_ATTACK,
        Triggers.PROJECTILE_HIT,
        Triggers.TAKE_ENTITY_DAMAGE,
        Triggers.KILL,
        Triggers.FALL_DAMAGE,
        Triggers.JUMP,
        Triggers.MINE_BLOCK
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val location = data.location ?: return
        val world = location.world ?: return

        for (i in 1..config.getInt("amount")) {
            plugin.scheduler.runLater({
                world.strikeLightning(location)
            }, 1)
        }
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getIntOrNull("amount")
            ?: violations.add(
                ConfigViolation(
                    "amount",
                    "You must specify the amount of lightning!"
                )
            )

        return violations
    }
}