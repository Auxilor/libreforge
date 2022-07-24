package com.willfp.libreforge.integrations.ecobosses

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.events.BossTryDropItemEvent
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class EffectBossDropChanceMultiplier : Effect("boss_drop_chance_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableMap<String, MutableList<MultiplierModifier>>>()
    private val globalModifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        if (config.has("bosses")) {
            val bosses = config.getStrings("bosses")
            for (boss in bosses) {
                val bossMultipliers = modifiers[player.uniqueId] ?: mutableMapOf()
                val registeredModifiers = bossMultipliers[boss] ?: mutableListOf()
                val uuid = identifiers.uuid
                registeredModifiers.removeIf { it.uuid == uuid }
                registeredModifiers.add(
                    MultiplierModifier(
                        uuid,
                        config.getDoubleFromExpression("multiplier", player)
                    )
                )
                bossMultipliers[boss] = registeredModifiers
                modifiers[player.uniqueId] = bossMultipliers
            }
        } else {
            val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
            val uuid = identifiers.uuid
            registeredModifiers.removeIf { it.uuid == uuid }
            registeredModifiers.add(
                MultiplierModifier(
                    uuid,
                    config.getDoubleFromExpression("multiplier", player)
                )
            )
            globalModifiers[player.uniqueId] = registeredModifiers
        }
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
        val uuid = identifiers.uuid
        registeredModifiers.removeIf { it.uuid == uuid }
        globalModifiers[player.uniqueId] = registeredModifiers


        for (boss in Bosses.values()) {
            val bossModifierMap = modifiers[player.uniqueId] ?: mutableMapOf()
            val registeredSkillModifiers = bossModifierMap[boss.id] ?: mutableListOf()
            registeredSkillModifiers.removeIf { it.uuid == uuid }
            bossModifierMap[boss.id] = registeredSkillModifiers
            modifiers[player.uniqueId] = bossModifierMap
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BossTryDropItemEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player ?: return

        var multiplier = 1.0

        for (modifier in (globalModifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        for (modifier in (modifiers[player.uniqueId]?.get(event.boss.id) ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        event.chance *= multiplier
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the chance multiplier!"
            )
        )

        return violations
    }
}
