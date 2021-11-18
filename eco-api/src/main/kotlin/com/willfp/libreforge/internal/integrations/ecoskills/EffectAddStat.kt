package com.willfp.libreforge.internal.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.api.effects.Effect
import com.willfp.libreforge.api.effects.getEffectAmount
import org.bukkit.entity.Player

class EffectAddStat : Effect("add_stat") {
    private val api = EcoSkillsAPI.getInstance()

    override fun handleEnable(
        player: Player,
        config: JSONConfig
    ) {
        api.addStatModifier(
            player,
            PlayerStatModifier(
                this.getNamespacedKey(player.getEffectAmount(this)),
                Stats.getByID(config.getString("stat", false)) ?: Stats.STRENGTH,
                config.getInt("amount")
            )
        )
    }

    override fun handleDisable(player: Player) {
        api.removeStatModifier(
            player,
            PlayerStatModifier(
                this.getNamespacedKey(player.getEffectAmount(this)),
                Stats.STRENGTH,
                0
            )
        )
    }
}