package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.MultiplierModifier
import com.willfp.libreforge.effects.getEffectAmount
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class EffectSkillXpMultiplier : Effect("skill_xp_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(player: Player, config: Config) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        registeredModifiers.add(
            MultiplierModifier(
                uuid,
                config.getDouble("multiplier")
            )
        )
        modifiers[player.uniqueId] = registeredModifiers
    }

    override fun handleDisable(player: Player) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        modifiers[player.uniqueId] = registeredModifiers
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        var multiplier = 1.0

        for (modifier in (modifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        val wrapped = WrappedSkillXpEvent(event)
        wrapped.amount = wrapped.amount * multiplier
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the xp multiplier!"
                )
            )

        return violations
    }
}
