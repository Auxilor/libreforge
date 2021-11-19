package com.willfp.libreforge.api.effects

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.ConfigViolation
import com.willfp.libreforge.api.LibReforge
import com.willfp.libreforge.api.filter.Filter
import com.willfp.libreforge.api.filter.Filters
import com.willfp.libreforge.internal.effects.EffectAttackSpeedMultiplier
import com.willfp.libreforge.internal.effects.EffectBonusHealth
import com.willfp.libreforge.internal.effects.EffectBowDamageMultiplier
import com.willfp.libreforge.internal.effects.EffectCritMultiplier
import com.willfp.libreforge.internal.effects.EffectDamageMultiplier
import com.willfp.libreforge.internal.effects.EffectFallDamageMultiplier
import com.willfp.libreforge.internal.effects.EffectIncomingDamageMultiplier
import com.willfp.libreforge.internal.effects.EffectKnockbackMultiplier
import com.willfp.libreforge.internal.effects.EffectMovementSpeedMultiplier
import com.willfp.libreforge.internal.effects.EffectRewardBlockBreak
import com.willfp.libreforge.internal.effects.EffectRewardKill
import com.willfp.libreforge.internal.effects.EffectTridentDamageMultiplier
import com.willfp.libreforge.internal.filter.CompoundFilter
import com.willfp.libreforge.internal.filter.FilterBlock
import com.willfp.libreforge.internal.filter.FilterEmpty
import com.willfp.libreforge.internal.filter.FilterLivingEntity
import it.unimi.dsi.fastutil.ints.Int2IntFunctions

object Effects {
    private val BY_ID = HashBiMap.create<String, Effect>()

    val DAMAGE_MULTIPLIER: Effect = EffectDamageMultiplier()
    val CRIT_MULTIPLIER: Effect = EffectCritMultiplier()
    val REWARD_KILL: Effect = EffectRewardKill()
    val KNOCKBACK_MULTIPLIER: Effect = EffectKnockbackMultiplier()
    val REWARD_BLOCK_BREAK: Effect = EffectRewardBlockBreak()
    val INCOMING_DAMAGE_MULTIPLIER: Effect = EffectIncomingDamageMultiplier()
    val ATTACK_SPEED_MULTIPLIER: Effect = EffectAttackSpeedMultiplier()
    val MOVEMENT_SPEED_MULTIPLIER: Effect = EffectMovementSpeedMultiplier()
    val BONUS_HEALTH: Effect = EffectBonusHealth()
    val BOW_DAMAGE_MULTIPLIER: Effect = EffectBowDamageMultiplier()
    val FALL_DAMAGE_MULTIPLIER: Effect = EffectFallDamageMultiplier()
    val TRIDENT_DAMAGE_MULTIPLIER: Effect = EffectTridentDamageMultiplier()

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
    fun compile(config: JSONConfig, context: String): ConfiguredEffect? {
        val effect = config.getString("id").let {
            val found = Effects.getByID(it)
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

        val filters = config.getSubsectionsOrNull("filters").let {
            if (!effect.supportsFilters && it != null) {
                LibReforge.logViolation(
                    effect.id,
                    context,
                    ConfigViolation("filters", "Filters specified on an effect that does not support them!")
                )

                return@let null
            }

            val builder = mutableListOf<Filter>()

            for (filterConfig in it ?: emptyList()) {
                val id = filterConfig.getString("id")
                val filter = Filters.createById(id, filterConfig)
                if (filter is FilterEmpty) {
                    LibReforge.logViolation(
                        effect.id,
                        context,
                        ConfigViolation(
                            "filters", "Invalid filter specified: $id"
                        )
                    )
                } else {
                    builder.add(filter)
                }
            }

            return@let CompoundFilter(*builder.toTypedArray())
        } ?: return null

        return ConfiguredEffect(effect, args, filters)
    }
}
