package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import org.bukkit.entity.Player
import java.util.UUID

abstract class GenericMultiMultiplierEffect<T : Any>(
    id: String,
    private val getElement: (String) -> T?,
    private val getAll: () -> Iterable<T>,
    private val key: String,
) : Effect(id) {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    private val modifiers = mutableMapOf<UUID, MutableMap<T, MutableList<MultiplierModifier>>>()
    private val globalModifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    final override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        if (config.has(key)) {
            val jobs = config.getStrings(key).mapNotNull { getElement(it) }
            for (job in jobs) {
                val skillModifiers = modifiers[player.uniqueId] ?: mutableMapOf()
                val registeredModifiers = skillModifiers[job] ?: mutableListOf()
                val uuid = identifiers.uuid
                registeredModifiers.removeIf { it.uuid == uuid }
                registeredModifiers.add(
                    MultiplierModifier(uuid) {
                        config.getDoubleFromExpression("multiplier", player)
                    }
                )
                skillModifiers[job] = registeredModifiers
                modifiers[player.uniqueId] = skillModifiers
            }
        } else {
            val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
            val uuid = identifiers.uuid
            registeredModifiers.removeIf { it.uuid == uuid }
            registeredModifiers.add(
                MultiplierModifier(uuid) {
                    config.getDoubleFromExpression("multiplier", player)
                }
            )
            globalModifiers[player.uniqueId] = registeredModifiers
        }
    }

    final override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
        val uuid = identifiers.uuid
        registeredModifiers.removeIf { it.uuid == uuid }
        globalModifiers[player.uniqueId] = registeredModifiers

        for (element in getAll()) {
            val elementModifierMap = modifiers[player.uniqueId] ?: mutableMapOf()
            val registeredSkillModifiers = elementModifierMap[element] ?: mutableListOf()

            registeredSkillModifiers.removeIf { it.uuid == uuid }

            elementModifierMap[element] = registeredSkillModifiers
            modifiers[player.uniqueId] = elementModifierMap
        }
    }

    fun getMultiplier(player: Player, element: T): Double {
        var multiplier = 1.0

        for (modifier in (globalModifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        for (modifier in (modifiers[player.uniqueId]?.get(element) ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        return multiplier
    }
}
