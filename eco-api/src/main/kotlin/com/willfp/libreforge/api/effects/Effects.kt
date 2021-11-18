package com.willfp.libreforge.api.effects

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
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
}
