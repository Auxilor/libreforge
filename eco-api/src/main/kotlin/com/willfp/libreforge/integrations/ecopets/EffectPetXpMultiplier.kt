package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecopets.api.event.PlayerPetExpGainEvent
import com.willfp.ecopets.pets.Pet
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.MultiplierModifier
import com.willfp.libreforge.effects.getEffectAmount
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class EffectPetXpMultiplier : Effect("pet_xp_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableMap<Pet, MutableList<MultiplierModifier>>>()
    private val globalModifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(player: Player, config: Config) {
        if (config.has("pets")) {
            val pets = config.getStrings("pets").mapNotNull { Pets.getByID(it) }
            for (pet in pets) {
                val petModifiers = modifiers[player.uniqueId] ?: mutableMapOf()
                val registeredModifiers = petModifiers[pet] ?: mutableListOf()
                val uuid = this.getUUID(player.getEffectAmount(this))
                registeredModifiers.removeIf { it.uuid == uuid }
                registeredModifiers.add(
                    MultiplierModifier(
                        uuid,
                        config.getDoubleFromExpression("multiplier", player)
                    )
                )
                petModifiers[pet] = registeredModifiers
                modifiers[player.uniqueId] = petModifiers
            }
        } else {
            val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
            val uuid = this.getUUID(player.getEffectAmount(this))
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

    override fun handleDisable(player: Player) {
        val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        globalModifiers[player.uniqueId] = registeredModifiers


        for (pet in Pets.values()) {
            val petModifierMap = modifiers[player.uniqueId] ?: mutableMapOf()
            val registeredPetModifiers = petModifierMap[pet] ?: mutableListOf()
            registeredPetModifiers.removeIf { it.uuid == uuid }
            petModifierMap[pet] = registeredPetModifiers
            modifiers[player.uniqueId] = petModifierMap
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerPetExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        var multiplier = 1.0

        for (modifier in (globalModifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }


        for (modifier in (modifiers[player.uniqueId]?.get(event.pet) ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        val wrapped = WrappedPetXpEvent(event)
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
