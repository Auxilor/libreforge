package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.impl.ConditionAboveBalance
import com.willfp.libreforge.conditions.impl.ConditionAboveGlobalPoints
import com.willfp.libreforge.conditions.impl.ConditionAboveHealthPercent
import com.willfp.libreforge.conditions.impl.ConditionAboveHungerPercent
import com.willfp.libreforge.conditions.impl.ConditionAbovePoints
import com.willfp.libreforge.conditions.impl.ConditionAboveXPLevel
import com.willfp.libreforge.conditions.impl.ConditionAboveY
import com.willfp.libreforge.conditions.impl.ConditionAnyOf
import com.willfp.libreforge.conditions.impl.ConditionAtLeastOf
import com.willfp.libreforge.conditions.impl.ConditionBelowBalance
import com.willfp.libreforge.conditions.impl.ConditionBelowGlobalPoints
import com.willfp.libreforge.conditions.impl.ConditionBelowHealthPercent
import com.willfp.libreforge.conditions.impl.ConditionBelowHungerPercent
import com.willfp.libreforge.conditions.impl.ConditionBelowPoints
import com.willfp.libreforge.conditions.impl.ConditionBelowXPLevel
import com.willfp.libreforge.conditions.impl.ConditionBelowY
import com.willfp.libreforge.conditions.impl.ConditionCanAffordPrice
import com.willfp.libreforge.conditions.impl.ConditionGlobalPointsEqual
import com.willfp.libreforge.conditions.impl.ConditionHasCompletedAdvancement
import com.willfp.libreforge.conditions.impl.ConditionHasItem
import com.willfp.libreforge.conditions.impl.ConditionHasItemData
import com.willfp.libreforge.conditions.impl.ConditionHasPermission
import com.willfp.libreforge.conditions.impl.ConditionHasPotionEffect
import com.willfp.libreforge.conditions.impl.ConditionInAir
import com.willfp.libreforge.conditions.impl.ConditionInBiome
import com.willfp.libreforge.conditions.impl.ConditionInBlock
import com.willfp.libreforge.conditions.impl.ConditionInMainhand
import com.willfp.libreforge.conditions.impl.ConditionInOffhand
import com.willfp.libreforge.conditions.impl.ConditionInWater
import com.willfp.libreforge.conditions.impl.ConditionInWorld
import com.willfp.libreforge.conditions.impl.ConditionIsExpressionTrue
import com.willfp.libreforge.conditions.impl.ConditionIsFalling
import com.willfp.libreforge.conditions.impl.ConditionIsFlying
import com.willfp.libreforge.conditions.impl.ConditionIsFrozen
import com.willfp.libreforge.conditions.impl.ConditionIsGliding
import com.willfp.libreforge.conditions.impl.ConditionIsNight
import com.willfp.libreforge.conditions.impl.ConditionIsOp
import com.willfp.libreforge.conditions.impl.ConditionIsSneaking
import com.willfp.libreforge.conditions.impl.ConditionIsSprinting
import com.willfp.libreforge.conditions.impl.ConditionIsStorm
import com.willfp.libreforge.conditions.impl.ConditionIsSwimming
import com.willfp.libreforge.conditions.impl.ConditionItemDataEquals
import com.willfp.libreforge.conditions.impl.ConditionItemLevelAbove
import com.willfp.libreforge.conditions.impl.ConditionItemLevelBelow
import com.willfp.libreforge.conditions.impl.ConditionItemLevelEquals
import com.willfp.libreforge.conditions.impl.ConditionItemPointsAbove
import com.willfp.libreforge.conditions.impl.ConditionItemPointsBelow
import com.willfp.libreforge.conditions.impl.ConditionItemPointsEqual
import com.willfp.libreforge.conditions.impl.ConditionLightLevelBelow
import com.willfp.libreforge.conditions.impl.ConditionNearEntity
import com.willfp.libreforge.conditions.impl.ConditionOnFire
import com.willfp.libreforge.conditions.impl.ConditionOnGround
import com.willfp.libreforge.conditions.impl.ConditionPlaceholderContains
import com.willfp.libreforge.conditions.impl.ConditionPlaceholderEquals
import com.willfp.libreforge.conditions.impl.ConditionPlaceholderGreaterThan
import com.willfp.libreforge.conditions.impl.ConditionPlaceholderLessThan
import com.willfp.libreforge.conditions.impl.ConditionPointsEqual
import com.willfp.libreforge.conditions.impl.ConditionRidingEntity
import com.willfp.libreforge.conditions.impl.ConditionStandingOnBlock
import com.willfp.libreforge.conditions.impl.ConditionWearingBoots
import com.willfp.libreforge.conditions.impl.ConditionWearingChestplate
import com.willfp.libreforge.conditions.impl.ConditionWearingHelmet
import com.willfp.libreforge.conditions.impl.ConditionWearingLeggings
import com.willfp.libreforge.conditions.impl.ConditionWithinRadiusOf
import com.willfp.libreforge.deprecationMessage
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.separatorAmbivalent

object Conditions : Registry<Condition<*>>() {
    /**
     * Get a condition by [id].
     *
     * This will enable the condition.
     */
    override fun get(id: String): Condition<*>? {
        return super.get(id)?.apply {
            enable()
        }
    }

    /**
     * Compile a list of [configs] into a ConditionList in a given [context].
     */
    fun compile(configs: Collection<Config>, context: ViolationContext): ConditionList =
        ConditionList(configs.mapNotNull { compile(it, context) })

    /**
     * Compile a [cfg] into a ConditionBlock in a given [context].
     */
    fun compile(cfg: Config, context: ViolationContext): ConditionBlock<*>? {
        val config = cfg.separatorAmbivalent()

        val condition = get(config.getString("id"))

        if (condition == null) {
            context.log(ConfigViolation("id", "Invalid condition ID specified!"))
            return null
        }

        if (condition.deprecationMessage != null) {
            context.log(
                ConfigWarning(
                    "id",
                    "Condition ${condition.id} is deprecated: ${condition.deprecationMessage}. It will be removed in the future."
                )
            )
        }

        val notMetEffects = Effects.compileChain(
            config.getSubsections("not-met-effects"),
            context.with("not-met-effects")
        )

        return makeBlock(condition, notMetEffects, config.getSubsection("args"), context.with("args"))
    }

    private fun <T> makeBlock(
        condition: Condition<T>,
        notMetEffects: Chain?,
        config: Config,
        context: ViolationContext
    ): ConditionBlock<T>? {
        if (!condition.checkConfig(config, context)) {
            return null
        }

        val compileData = condition.makeCompileData(config, context)

        return ConditionBlock(
            condition,
            config,
            compileData,
            notMetEffects,
            config.getStrings("not-met-lines"),
            config.getBool("show-not-met"),
            config.getBool("inverse")
        )
    }

    init {
        register(ConditionAboveBalance)
        register(ConditionAboveHealthPercent)
        register(ConditionAboveHungerPercent)
        register(ConditionAbovePoints)
        register(ConditionAboveXPLevel)
        register(ConditionAboveY)
        register(ConditionAnyOf)
        register(ConditionAtLeastOf)
        register(ConditionBelowBalance)
        register(ConditionBelowHealthPercent)
        register(ConditionBelowHungerPercent)
        register(ConditionBelowPoints)
        register(ConditionBelowXPLevel)
        register(ConditionBelowY)
        register(ConditionCanAffordPrice)
        register(ConditionHasItem)
        register(ConditionHasPermission)
        register(ConditionHasPotionEffect)
        register(ConditionInAir)
        register(ConditionInBiome)
        register(ConditionInBlock)
        register(ConditionInMainhand)
        register(ConditionInOffhand)
        register(ConditionInWater)
        register(ConditionInWorld)
        register(ConditionIsExpressionTrue)
        register(ConditionIsFrozen)
        register(ConditionIsGliding)
        register(ConditionIsNight)
        register(ConditionIsSneaking)
        register(ConditionIsSprinting)
        register(ConditionIsStorm)
        register(ConditionIsSwimming)
        register(ConditionNearEntity)
        register(ConditionOnFire)
        register(ConditionPlaceholderContains)
        register(ConditionPlaceholderEquals)
        register(ConditionPlaceholderGreaterThan)
        register(ConditionPlaceholderLessThan)
        register(ConditionPointsEqual)
        register(ConditionRidingEntity)
        register(ConditionStandingOnBlock)
        register(ConditionWearingBoots)
        register(ConditionWearingChestplate)
        register(ConditionWearingHelmet)
        register(ConditionWearingLeggings)
        register(ConditionWithinRadiusOf)
        register(ConditionAboveGlobalPoints)
        register(ConditionBelowGlobalPoints)
        register(ConditionGlobalPointsEqual)
        register(ConditionItemLevelAbove)
        register(ConditionItemLevelBelow)
        register(ConditionItemLevelEquals)
        register(ConditionItemPointsAbove)
        register(ConditionItemPointsBelow)
        register(ConditionItemPointsEqual)
        register(ConditionOnGround)
        register(ConditionIsFalling)
        register(ConditionIsFlying)
        register(ConditionHasItemData)
        register(ConditionItemDataEquals)
        register(ConditionIsOp)
        register(ConditionHasCompletedAdvancement)
        register(ConditionLightLevelBelow)
    }
}
