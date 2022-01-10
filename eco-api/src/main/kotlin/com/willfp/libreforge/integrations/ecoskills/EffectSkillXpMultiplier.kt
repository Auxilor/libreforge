package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.MultiplierModifier
import com.willfp.libreforge.effects.getEffectAmount
import com.willfp.libreforge.getDouble
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class EffectSkillXpMultiplier : Effect("skill_xp_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableMap<Skill, MutableList<MultiplierModifier>>>()
    private val globalModifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(player: Player, config: Config) {
        if (config.has("skills")) {
            val skills = config.getStrings("skills").mapNotNull { Skills.getByID(it) }
            for (skill in skills) {
                val skillModifiers = modifiers[player.uniqueId] ?: mutableMapOf()
                val registeredModifiers = skillModifiers[skill] ?: mutableListOf()
                val uuid = this.getUUID(player.getEffectAmount(this))
                registeredModifiers.removeIf { it.uuid == uuid }
                registeredModifiers.add(
                    MultiplierModifier(
                        uuid,
                        config.getDouble("multiplier", player)
                    )
                )
                skillModifiers[skill] = registeredModifiers
                modifiers[player.uniqueId] = skillModifiers
            }
        } else {
            val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
            val uuid = this.getUUID(player.getEffectAmount(this))
            registeredModifiers.removeIf { it.uuid == uuid }
            registeredModifiers.add(
                MultiplierModifier(
                    uuid,
                    config.getDouble("multiplier", player)
                )
            )
            globalModifiers[player.uniqueId] = registeredModifiers
        }


    }

    override fun handleDisable(player: Player) {
        val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        globalModifiers[player.uniqueId] = registeredModifiers


        for (skill in Skills.values()) {
            val skillModifierMap = modifiers[player.uniqueId] ?: mutableMapOf()
            val registeredSkillModifiers = skillModifierMap[skill] ?: mutableListOf()
            registeredSkillModifiers.removeIf { it.uuid == uuid }
            skillModifierMap[skill] = registeredSkillModifiers
            modifiers[player.uniqueId] = skillModifierMap
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillExpGainEvent) {
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

        val wrapped = WrappedSkillXpEvent(event)
        wrapped.amount = wrapped.amount * multiplier
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
