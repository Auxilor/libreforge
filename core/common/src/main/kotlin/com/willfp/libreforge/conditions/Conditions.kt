package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.impl.*
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
        register(ConditionInSlot)
        register(ConditionHasEnchant)
        register(ConditionIsAlive)
        register(ConditionIsSubmerged)
        register(ConditionInGamemode)
    }
}
