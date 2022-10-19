package com.willfp.libreforge.conditions

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.conditions.conditions.ConditionAboveBalance
import com.willfp.libreforge.conditions.conditions.ConditionAboveHealthPercent
import com.willfp.libreforge.conditions.conditions.ConditionAboveHungerPercent
import com.willfp.libreforge.conditions.conditions.ConditionAbovePoints
import com.willfp.libreforge.conditions.conditions.ConditionAboveXPLevel
import com.willfp.libreforge.conditions.conditions.ConditionAboveY
import com.willfp.libreforge.conditions.conditions.ConditionAnyOf
import com.willfp.libreforge.conditions.conditions.ConditionBelowBalance
import com.willfp.libreforge.conditions.conditions.ConditionBelowHealthPercent
import com.willfp.libreforge.conditions.conditions.ConditionBelowHungerPercent
import com.willfp.libreforge.conditions.conditions.ConditionBelowPoints
import com.willfp.libreforge.conditions.conditions.ConditionBelowXPLevel
import com.willfp.libreforge.conditions.conditions.ConditionBelowY
import com.willfp.libreforge.conditions.conditions.ConditionHasItem
import com.willfp.libreforge.conditions.conditions.ConditionHasPermission
import com.willfp.libreforge.conditions.conditions.ConditionHasPotionEffect
import com.willfp.libreforge.conditions.conditions.ConditionInAir
import com.willfp.libreforge.conditions.conditions.ConditionInBiome
import com.willfp.libreforge.conditions.conditions.ConditionInBlock
import com.willfp.libreforge.conditions.conditions.ConditionInMainhand
import com.willfp.libreforge.conditions.conditions.ConditionInOffhand
import com.willfp.libreforge.conditions.conditions.ConditionInWater
import com.willfp.libreforge.conditions.conditions.ConditionInWorld
import com.willfp.libreforge.conditions.conditions.ConditionIsExpressionTrue
import com.willfp.libreforge.conditions.conditions.ConditionIsFrozen
import com.willfp.libreforge.conditions.conditions.ConditionIsGliding
import com.willfp.libreforge.conditions.conditions.ConditionIsNight
import com.willfp.libreforge.conditions.conditions.ConditionIsSneaking
import com.willfp.libreforge.conditions.conditions.ConditionIsSprinting
import com.willfp.libreforge.conditions.conditions.ConditionIsStorm
import com.willfp.libreforge.conditions.conditions.ConditionIsSwimming
import com.willfp.libreforge.conditions.conditions.ConditionOnFire
import com.willfp.libreforge.conditions.conditions.ConditionPlaceholderContains
import com.willfp.libreforge.conditions.conditions.ConditionPlaceholderEquals
import com.willfp.libreforge.conditions.conditions.ConditionPlaceholderGreaterThan
import com.willfp.libreforge.conditions.conditions.ConditionPlaceholderLessThan
import com.willfp.libreforge.conditions.conditions.ConditionPointsEqual
import com.willfp.libreforge.conditions.conditions.ConditionRidingEntity
import com.willfp.libreforge.conditions.conditions.ConditionStandingOnBlock
import com.willfp.libreforge.conditions.conditions.ConditionWearingBoots
import com.willfp.libreforge.conditions.conditions.ConditionWearingChestplate
import com.willfp.libreforge.conditions.conditions.ConditionWearingHelmet
import com.willfp.libreforge.conditions.conditions.ConditionWearingLeggings
import com.willfp.libreforge.conditions.conditions.ConditionWithinRadiusOf
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.separatorAmbivalent

@Suppress("UNUSED")
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
    val IS_NIGHT: Condition = ConditionIsNight()
    val IS_STORM: Condition = ConditionIsStorm()
    val PLACEHOLDER_EQUALS: Condition = ConditionPlaceholderEquals()
    val PLACEHOLDER_GREATER_THAN: Condition = ConditionPlaceholderGreaterThan()
    val PLACEHOLDER_LESS_THAN: Condition = ConditionPlaceholderLessThan()
    val ABOVE_BALANCE: Condition = ConditionAboveBalance()
    val BELOW_BALANCE: Condition = ConditionBelowBalance()
    val IS_GLIDING: Condition = ConditionIsGliding()
    val ON_FIRE: Condition = ConditionOnFire()
    val STANDING_ON_BLOCK: Condition = ConditionStandingOnBlock()
    val RIDING_ENTITY: Condition = ConditionRidingEntity()
    val ABOVE_POINTS: Condition = ConditionAbovePoints()
    val BELOW_POINTS: Condition = ConditionBelowPoints()
    val POINTS_EQUAL: Condition = ConditionPointsEqual()
    val HAS_POTION_EFFECT: Condition = ConditionHasPotionEffect()
    val WITHIN_RADIUS_OF: Condition = ConditionWithinRadiusOf()
    val IS_SPRINTING: Condition = ConditionIsSprinting()
    val ANY_OF: Condition = ConditionAnyOf()
    val PLACEHOLDER_CONTAINS: Condition = ConditionPlaceholderContains()
    val IS_FROZEN: Condition = ConditionIsFrozen()
    val IN_MAINHAND: Condition = ConditionInMainhand()
    val IN_OFFHAND: Condition = ConditionInOffhand()
    val WEARING_BOOTS: Condition = ConditionWearingBoots()
    val WEARING_LEGGINGS: Condition = ConditionWearingLeggings()
    val WEARING_CHESTPLATE: Condition = ConditionWearingChestplate()
    val WEARING_HELMET: Condition = ConditionWearingHelmet()
    val HAS_ITEM: Condition = ConditionHasItem()
    val IS_EXPRESSION_TRUE: Condition = ConditionIsExpressionTrue()
    val IN_BLOCK: Condition = ConditionInBlock()
    val IS_SWIMMING: Condition = ConditionIsSwimming()

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
     * Compile a group of conditions.
     *
     * @param configs The condition configs.
     * @param context The context to log violations for.
     * @return The compiled conditions.
     */
    @JvmStatic
    fun compile(
        configs: Iterable<Config>,
        context: String
    ): Set<ConfiguredCondition> = configs.mapNotNull { compile(it, context) }.toSet()

    /**
     * Compile a condition.
     *
     * @param cfg The config for the condition.
     * @param context The context to log violations for.
     * @return The configured condition, or null if invalid.
     */
    @JvmStatic
    fun compile(cfg: Config, context: String): ConfiguredCondition? {
        val config = cfg.separatorAmbivalent()

        val condition = config.getString("id").let {
            val found = getByID(it)
            if (found == null) {
                LibReforgePlugin.instance.logViolation(
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

        val notMetLines = args.getFormattedStringsOrNull("not-met-lines")

        val compileData = condition.makeCompileData(args, context)

        val inverse = args.getBool("inverse")

        val notMetEffects = Effects.compile(
            config.getSubsections("not-met-effects"),
            "$context -> Not Met Effects)",
            chainLike = true
        )

        return ConfiguredCondition(condition, args, notMetLines, notMetEffects, compileData, inverse)
    }
}
