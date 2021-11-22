package com.willfp.libreforge.conditions

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforge
import com.willfp.libreforge.conditions.conditions.*

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

    /**
     * Compile a condition.
     *
     * @param config The config for the condition.
     * @param context The context to log violations for.
     *
     * @return The configured condition, or null if invalid.
     */
    @JvmStatic
    fun compile(config: JSONConfig, context: String): ConfiguredCondition? {
        val condition = config.getString("id").let {
            val found = getByID(it)
            if (found == null) {
                LibReforge.logViolation(
                    it,
                    context,
                    ConfigViolation("id", "Invalid condition ID specified!")
                )
            }

            found
        } ?: return null

        val args = config.getSubsection("args")
        if (condition.checkConfig(args, context)) {
            return null
        }

        return ConfiguredCondition(condition, args)
    }
}
