package com.willfp.libreforge.effects

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforge
import com.willfp.libreforge.effects.effects.EffectArmor
import com.willfp.libreforge.effects.effects.EffectArmorToughness
import com.willfp.libreforge.effects.effects.EffectArrowRing
import com.willfp.libreforge.effects.effects.EffectAttackSpeedMultiplier
import com.willfp.libreforge.effects.effects.EffectAutosmelt
import com.willfp.libreforge.effects.effects.EffectBleed
import com.willfp.libreforge.effects.effects.EffectBonusHealth
import com.willfp.libreforge.effects.effects.EffectCancelEvent
import com.willfp.libreforge.effects.effects.EffectCritMultiplier
import com.willfp.libreforge.effects.effects.EffectDamageMultiplier
import com.willfp.libreforge.effects.effects.EffectFoodMultiplier
import com.willfp.libreforge.effects.effects.EffectGiveFood
import com.willfp.libreforge.effects.effects.EffectGiveHealth
import com.willfp.libreforge.effects.effects.EffectGiveMoney
import com.willfp.libreforge.effects.effects.EffectGiveXp
import com.willfp.libreforge.effects.effects.EffectHungerMultiplier
import com.willfp.libreforge.effects.effects.EffectKnockbackMultiplier
import com.willfp.libreforge.effects.effects.EffectMovementSpeedMultiplier
import com.willfp.libreforge.effects.effects.EffectPermanentPotionEffect
import com.willfp.libreforge.effects.effects.EffectPotionEffect
import com.willfp.libreforge.effects.effects.EffectRegenMultiplier
import com.willfp.libreforge.effects.effects.EffectRunCommand
import com.willfp.libreforge.effects.effects.EffectSendMessage
import com.willfp.libreforge.effects.effects.EffectSpawnMobs
import com.willfp.libreforge.effects.effects.EffectStrikeLightning
import com.willfp.libreforge.effects.effects.EffectTeleport
import com.willfp.libreforge.effects.effects.EffectXpMultiplier
import com.willfp.libreforge.filters.ConfiguredFilter
import com.willfp.libreforge.filters.EmptyFilter
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.Triggers
import java.util.UUID

@Suppress("UNUSED")
object Effects {
    private val BY_ID = HashBiMap.create<String, Effect>()

    val DAMAGE_MULTIPLIER: Effect = EffectDamageMultiplier()
    val CRIT_MULTIPLIER: Effect = EffectCritMultiplier()
    val KNOCKBACK_MULTIPLIER: Effect = EffectKnockbackMultiplier()
    val GIVE_MONEY: Effect = EffectGiveMoney()
    val ATTACK_SPEED_MULTIPLIER: Effect = EffectAttackSpeedMultiplier()
    val MOVEMENT_SPEED_MULTIPLIER: Effect = EffectMovementSpeedMultiplier()
    val BONUS_HEALTH: Effect = EffectBonusHealth()
    val RUN_COMMAND: Effect = EffectRunCommand()
    val STRIKE_LIGHTNING: Effect = EffectStrikeLightning()
    val SPAWN_MOBS: Effect = EffectSpawnMobs()
    val HUNGER_MULTIPLIER: Effect = EffectHungerMultiplier()
    val REGEN_MULTIPLIER: Effect = EffectRegenMultiplier()
    val PERMANENT_POTION_EFFECT: Effect = EffectPermanentPotionEffect()
    val POTION_EFFECT: Effect = EffectPotionEffect()
    val ARMOR: Effect = EffectArmor()
    val ARMOR_TOUGHNESS: Effect = EffectArmorToughness()
    val GIVE_XP: Effect = EffectGiveXp()
    val XP_MULTIPLIER: Effect = EffectXpMultiplier()
    val BLEED: Effect = EffectBleed()
    val ARROW_RING: Effect = EffectArrowRing()
    val FOOD_MULTIPLIER: Effect = EffectFoodMultiplier()
    val AUTOSMELT: Effect = EffectAutosmelt()
    val TELEPORT: Effect = EffectTeleport()
    val CANCEL_EVENT: Effect = EffectCancelEvent()
    val SEND_MESSAGE: Effect = EffectSendMessage()
    val GIVE_FOOD: Effect = EffectGiveFood()
    val GIVE_HEALTH: Effect = EffectGiveHealth()

    /**
     * Get effect matching id.
     *
     * @param id The id to query.
     * @return The matching effect, or null if not found.
     */
    fun getByID(id: String): Effect? {
        return BY_ID[id]
    }

    /**
     * List of all registered effects.
     *
     * @return The effects.
     */
    fun values(): List<Effect> {
        return ImmutableList.copyOf(BY_ID.values)
    }

    /**
     * Add new effect.
     *
     * @param effect The effect to add.
     */
    fun addNewEffect(effect: Effect) {
        BY_ID.remove(effect.id)
        BY_ID[effect.id] = effect
    }

    /**
     * Compile an effect.
     *
     * @param config The config for the effect.
     * @param context The context to log violations for.
     *
     * @return The configured effect, or null if invalid.
     */
    @JvmStatic
    fun compile(config: Config, context: String): ConfiguredEffect? {
        val effect = config.getString("id").let {
            val found = getByID(it)
            if (found == null) {
                LibReforge.logViolation(
                    it,
                    context,
                    ConfigViolation("id", "Invalid effect ID specified!")
                )
            }

            found
        } ?: return null

        val args = config.getSubsection("args")
        if (effect.checkConfig(args, context)) {
            return null
        }

        val filter = config.getSubsectionOrNull("filters").let {
            if (!effect.supportsFilters && it != null) {
                LibReforge.logViolation(
                    effect.id,
                    context,
                    ConfigViolation("filters", "Specified effect does not support filters")
                )

                return@let null
            }

            if (it == null) EmptyFilter() else ConfiguredFilter(it)
        } ?: return null

        val triggers = config.getStrings("triggers").let {
            val triggers = mutableListOf<Trigger>()

            if (it.isNotEmpty() && effect.applicableTriggers.isEmpty()) {
                LibReforge.logViolation(
                    effect.id,
                    context,
                    ConfigViolation(
                        "triggers", "Specified effect does not support triggers"
                    )
                )

                return@let null
            }

            if (effect.applicableTriggers.isNotEmpty() && it.isEmpty()) {
                LibReforge.logViolation(
                    effect.id,
                    context,
                    ConfigViolation(
                        "triggers", "Specified effect requires at least 1 trigger"
                    )
                )

                return@let null
            }

            for (id in it) {
                val trigger = Triggers.getById(id)

                if (trigger == null) {
                    LibReforge.logViolation(
                        effect.id,
                        context,
                        ConfigViolation(
                            "triggers", "Invalid trigger specified: $id"
                        )
                    )

                    return@let null
                }

                if (!effect.applicableTriggers.contains(trigger)) {
                    LibReforge.logViolation(
                        effect.id,
                        context,
                        ConfigViolation(
                            "triggers", "Specified effect does not support trigger $id"
                        )
                    )
                }

                triggers.add(trigger)
            }

            triggers
        } ?: return null

        return ConfiguredEffect(effect, args, filter, triggers, UUID.randomUUID())
    }
}
