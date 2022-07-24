package com.willfp.libreforge.integrations.mcmmo

import com.gmail.nossr50.datatypes.skills.PrimarySkillType
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent
import com.gmail.nossr50.mcMMO
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class EffectMcMMOXpMultiplier : Effect("mcmmo_xp_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableMap<PrimarySkillType, MutableList<MultiplierModifier>>>()
    private val globalModifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        if (config.has("skills")) {
            val skills = config.getStrings("skills").mapNotNull { mcMMO.p.skillTools.matchSkill(it) }
            for (skill in skills) {
                val skillModifiers = modifiers[player.uniqueId] ?: mutableMapOf()
                val registeredModifiers = skillModifiers[skill] ?: mutableListOf()
                val uuid = identifiers.uuid
                registeredModifiers.removeIf { it.uuid == uuid }
                registeredModifiers.add(
                    MultiplierModifier(
                        uuid,
                        config.getDoubleFromExpression("multiplier", player)
                    )
                )
                skillModifiers[skill] = registeredModifiers
                modifiers[player.uniqueId] = skillModifiers
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


        for (skill in PrimarySkillType.values()) {
            val skillModifierMap = modifiers[player.uniqueId] ?: mutableMapOf()
            val registeredSkillModifiers = skillModifierMap[skill] ?: mutableListOf()
            registeredSkillModifiers.removeIf { it.uuid == uuid }
            skillModifierMap[skill] = registeredSkillModifiers
            modifiers[player.uniqueId] = skillModifierMap
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerXpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        var multiplier = 1.0

        for (modifier in (globalModifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }


        for (modifier in (modifiers[player.uniqueId]?.get(event.skill) ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        event.rawXpGained = (event.rawXpGained * multiplier).toFloat()
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the xp multiplier!"
            )
        )

        return violations
    }
}
