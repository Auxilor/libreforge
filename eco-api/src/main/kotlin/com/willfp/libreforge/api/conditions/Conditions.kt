package com.willfp.libreforge.api.conditions

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.libreforge.internal.conditions.ConditionAboveHealthPercent
import com.willfp.libreforge.internal.conditions.ConditionAboveHungerPercent
import com.willfp.libreforge.internal.conditions.ConditionAboveXPLevel
import com.willfp.libreforge.internal.conditions.ConditionAboveY
import com.willfp.libreforge.internal.conditions.ConditionBelowHealthPercent
import com.willfp.libreforge.internal.conditions.ConditionBelowHungerPercent
import com.willfp.libreforge.internal.conditions.ConditionBelowXPLevel
import com.willfp.libreforge.internal.conditions.ConditionBelowY
import com.willfp.libreforge.internal.conditions.ConditionHasPermission
import com.willfp.libreforge.internal.conditions.ConditionInAir
import com.willfp.libreforge.internal.conditions.ConditionInBiome
import com.willfp.libreforge.internal.conditions.ConditionInWater
import com.willfp.libreforge.internal.conditions.ConditionInWorld
import com.willfp.libreforge.internal.conditions.ConditionIsSneaking

object Conditions {
    private val BY_ID = HashBiMap.create<String, Condition>()

    val BELOW_Y: Condition = ConditionBelowY()
    val ABOVE_Y: Condition = ConditionAboveY()
    val ABOVE_HEALTH_PERCENT: Condition = ConditionAboveHealthPercent()
    val BELOW_HEALTH_PERCENT: Condition = ConditionBelowHealthPercent()
    val IN_WATER: Condition = ConditionInWater()
    val IN_WORLD: Condition = ConditionInWorld()
    val ABOVE_XP_LEVEL: Condition = ConditionAboveXPLevel()
    val BELOW_XP_LEVEL: Condition = ConditionBelowXPLevel()
    val ABOVE_HUNGER_PERCENT: Condition = ConditionAboveHungerPercent()
    val BELOW_HUNGER_PERCENT: Condition = ConditionBelowHungerPercent()
    val IN_BIOME: Condition = ConditionInBiome()
    val HAS_PERMISSION: Condition = ConditionHasPermission()
    val IS_SNEAKING: Condition = ConditionIsSneaking()
    val IN_AIR: Condition = ConditionInAir()

    /**
     * Get condition matching id.
     *
     * @param id The id to query.
     * @return The matching condition, or null if not found.
     */
    fun getByID(id: String): Condition? {
        return BY_ID[id]
    }

    /**
     * List of all registered conditions.
     *
     * @return The conditions.
     */
    fun values(): List<Condition> {
        return ImmutableList.copyOf(BY_ID.values)
    }

    /**
     * Add new condition.
     *
     * @param condition The condition to add.
     */
    fun addNewCondition(condition: Condition) {
        BY_ID.remove(condition.id)
        BY_ID[condition.id] = condition
    }
}
