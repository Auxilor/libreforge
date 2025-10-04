@file:Suppress("DEPRECATION")

package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.deprecationMessage
import com.willfp.libreforge.effects.arguments.EffectArguments
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.effects.impl.EffectAOE
import com.willfp.libreforge.effects.impl.EffectAOEBlocks
import com.willfp.libreforge.effects.impl.EffectAddDamage
import com.willfp.libreforge.effects.impl.EffectAddDurability
import com.willfp.libreforge.effects.impl.EffectAddEnchant
import com.willfp.libreforge.effects.impl.EffectAddGlobalPoints
import com.willfp.libreforge.effects.impl.EffectAddHolder
import com.willfp.libreforge.effects.impl.EffectAddHolderInRadius
import com.willfp.libreforge.effects.impl.EffectAddHolderToVictim
import com.willfp.libreforge.effects.impl.EffectAddPermanentHolderInRadius
import com.willfp.libreforge.effects.impl.EffectAddPoints
import com.willfp.libreforge.effects.impl.EffectAgeCrop
import com.willfp.libreforge.effects.impl.EffectAllPlayers
import com.willfp.libreforge.effects.impl.EffectAnimation
import com.willfp.libreforge.effects.impl.EffectArmor
import com.willfp.libreforge.effects.impl.EffectArmorToughness
import com.willfp.libreforge.effects.impl.EffectArrowRing
import com.willfp.libreforge.effects.impl.EffectAttackSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectAutosmelt
import com.willfp.libreforge.effects.impl.EffectBleed
import com.willfp.libreforge.effects.impl.EffectBlockCommands
import com.willfp.libreforge.effects.impl.EffectBlockReach
import com.willfp.libreforge.effects.impl.EffectBonusHealth
import com.willfp.libreforge.effects.impl.EffectBreakBlock
import com.willfp.libreforge.effects.impl.EffectBrewTimeMultiplier
import com.willfp.libreforge.effects.impl.EffectBroadcast
import com.willfp.libreforge.effects.impl.EffectBurningTimeMultiplier
import com.willfp.libreforge.effects.impl.EffectCancelEvent
import com.willfp.libreforge.effects.impl.EffectClearInvulnerability
import com.willfp.libreforge.effects.impl.EffectCloseInventory
import com.willfp.libreforge.effects.impl.EffectConsumeHeldItem
import com.willfp.libreforge.effects.impl.EffectCreateBossBar
import com.willfp.libreforge.effects.impl.EffectCreateExplosion
import com.willfp.libreforge.effects.impl.EffectCreateHologram
import com.willfp.libreforge.effects.impl.EffectCritMultiplier
import com.willfp.libreforge.effects.impl.EffectDamageArmor
import com.willfp.libreforge.effects.impl.EffectDamageItem
import com.willfp.libreforge.effects.impl.EffectDamageMainhand
import com.willfp.libreforge.effects.impl.EffectDamageMultiplier
import com.willfp.libreforge.effects.impl.EffectDamageNearbyEntities
import com.willfp.libreforge.effects.impl.EffectDamageOffhand
import com.willfp.libreforge.effects.impl.EffectDamageTwice
import com.willfp.libreforge.effects.impl.EffectDamageVictim
import com.willfp.libreforge.effects.impl.EffectDontConsumeLapisChance
import com.willfp.libreforge.effects.impl.EffectDontConsumeXpChance
import com.willfp.libreforge.effects.impl.EffectDrill
import com.willfp.libreforge.effects.impl.EffectDropItem
import com.willfp.libreforge.effects.impl.EffectDropItemForPlayer
import com.willfp.libreforge.effects.impl.EffectDropItemSlot
import com.willfp.libreforge.effects.impl.EffectDropRandomItem
import com.willfp.libreforge.effects.impl.EffectDropRandomItemForPlayer
import com.willfp.libreforge.effects.impl.EffectDropWeightedRandomItem
import com.willfp.libreforge.effects.impl.EffectEntityReach
import com.willfp.libreforge.effects.impl.EffectExplosionKnockbackResistanceMultiplier
import com.willfp.libreforge.effects.impl.EffectExtinguish
import com.willfp.libreforge.effects.impl.EffectFeatherStep
import com.willfp.libreforge.effects.impl.EffectFlight
import com.willfp.libreforge.effects.impl.EffectFoodMultiplier
import com.willfp.libreforge.effects.impl.EffectGiveFood
import com.willfp.libreforge.effects.impl.EffectGiveGlobalPoints
import com.willfp.libreforge.effects.impl.EffectGiveHealth
import com.willfp.libreforge.effects.impl.EffectGiveItem
import com.willfp.libreforge.effects.impl.EffectGiveItemPoints
import com.willfp.libreforge.effects.impl.EffectGiveMoney
import com.willfp.libreforge.effects.impl.EffectGiveOxygen
import com.willfp.libreforge.effects.impl.EffectGivePoints
import com.willfp.libreforge.effects.impl.EffectGivePrice
import com.willfp.libreforge.effects.impl.EffectGiveSaturation
import com.willfp.libreforge.effects.impl.EffectGiveXp
import com.willfp.libreforge.effects.impl.EffectGlowNearbyBlocks
import com.willfp.libreforge.effects.impl.EffectGravityMultiplier
import com.willfp.libreforge.effects.impl.EffectHoming
import com.willfp.libreforge.effects.impl.EffectHungerMultiplier
import com.willfp.libreforge.effects.impl.EffectIgnite
import com.willfp.libreforge.effects.impl.EffectIncreaseStepHeight
import com.willfp.libreforge.effects.impl.EffectItemDurabilityMultiplier
import com.willfp.libreforge.effects.impl.EffectJumpStrengthMultiplier
import com.willfp.libreforge.effects.impl.EffectKeepInventory
import com.willfp.libreforge.effects.impl.EffectKeepLevel
import com.willfp.libreforge.effects.impl.EffectKick
import com.willfp.libreforge.effects.impl.EffectKnockAway
import com.willfp.libreforge.effects.impl.EffectKnockbackMultiplier
import com.willfp.libreforge.effects.impl.EffectKnockbackResistanceMultiplier
import com.willfp.libreforge.effects.impl.EffectLevelItem
import com.willfp.libreforge.effects.impl.EffectLuckMultiplier
import com.willfp.libreforge.effects.impl.EffectMineRadius
import com.willfp.libreforge.effects.impl.EffectMineRadiusOneDeep
import com.willfp.libreforge.effects.impl.EffectMineVein
import com.willfp.libreforge.effects.impl.EffectMiningEfficiency
import com.willfp.libreforge.effects.impl.EffectMiningSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectMovementEfficiencyMultiplier
import com.willfp.libreforge.effects.impl.EffectMovementSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectMultiplyDrops
import com.willfp.libreforge.effects.impl.EffectMultiplyGlobalPoints
import com.willfp.libreforge.effects.impl.EffectMultiplyItemPoints
import com.willfp.libreforge.effects.impl.EffectMultiplyPoints
import com.willfp.libreforge.effects.impl.EffectMultiplyVelocity
import com.willfp.libreforge.effects.impl.EffectNameEntity
import com.willfp.libreforge.effects.impl.EffectOpenCrafting
import com.willfp.libreforge.effects.impl.EffectOpenEnderChest
import com.willfp.libreforge.effects.impl.EffectParticleAnimation
import com.willfp.libreforge.effects.impl.EffectParticleLine
import com.willfp.libreforge.effects.impl.EffectPayPrice
import com.willfp.libreforge.effects.impl.EffectPermanentPotionEffect
import com.willfp.libreforge.effects.impl.EffectPiercing
import com.willfp.libreforge.effects.impl.EffectPlaySound
import com.willfp.libreforge.effects.impl.EffectPotionDurationMultiplier
import com.willfp.libreforge.effects.impl.EffectPotionEffect
import com.willfp.libreforge.effects.impl.EffectPullIn
import com.willfp.libreforge.effects.impl.EffectPullToLocation
import com.willfp.libreforge.effects.impl.EffectRandomPlayer
import com.willfp.libreforge.effects.impl.EffectRapidBows
import com.willfp.libreforge.effects.impl.EffectReelSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectRegenMultiplier
import com.willfp.libreforge.effects.impl.EffectRemoveBossBar
import com.willfp.libreforge.effects.impl.EffectRemoveEnchant
import com.willfp.libreforge.effects.impl.EffectRemoveItem
import com.willfp.libreforge.effects.impl.EffectRemoveItemData
import com.willfp.libreforge.effects.impl.EffectRemovePotionEffect
import com.willfp.libreforge.effects.impl.EffectRepairItem
import com.willfp.libreforge.effects.impl.EffectReplaceNear
import com.willfp.libreforge.effects.impl.EffectReplantCrops
import com.willfp.libreforge.effects.impl.EffectRotate
import com.willfp.libreforge.effects.impl.EffectRotateVictim
import com.willfp.libreforge.effects.impl.EffectRunChain
import com.willfp.libreforge.effects.impl.EffectRunChainInline
import com.willfp.libreforge.effects.impl.EffectRunCommand
import com.willfp.libreforge.effects.impl.EffectRunPlayerCommand
import com.willfp.libreforge.effects.impl.EffectSafeFallDistance
import com.willfp.libreforge.effects.impl.EffectScale
import com.willfp.libreforge.effects.impl.EffectSellItems
import com.willfp.libreforge.effects.impl.EffectSellMultiplier
import com.willfp.libreforge.effects.impl.EffectSendMessage
import com.willfp.libreforge.effects.impl.EffectSendTitle
import com.willfp.libreforge.effects.impl.EffectSetArmorTrim
import com.willfp.libreforge.effects.impl.EffectSetBlock
import com.willfp.libreforge.effects.impl.EffectSetCustomModelData
import com.willfp.libreforge.effects.impl.EffectSetFood
import com.willfp.libreforge.effects.impl.EffectSetFreezeTicks
import com.willfp.libreforge.effects.impl.EffectSetGlobalPoints
import com.willfp.libreforge.effects.impl.EffectSetItemData
import com.willfp.libreforge.effects.impl.EffectSetItemPoints
import com.willfp.libreforge.effects.impl.EffectSetPoints
import com.willfp.libreforge.effects.impl.EffectSetSaturation
import com.willfp.libreforge.effects.impl.EffectSetVelocity
import com.willfp.libreforge.effects.impl.EffectSetVictimVelocity
import com.willfp.libreforge.effects.impl.EffectShoot
import com.willfp.libreforge.effects.impl.EffectShootArrow
import com.willfp.libreforge.effects.impl.EffectShuffleHotbar
import com.willfp.libreforge.effects.impl.EffectSmite
import com.willfp.libreforge.effects.impl.EffectSneakingSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectSpawnEntity
import com.willfp.libreforge.effects.impl.EffectSpawnMobs
import com.willfp.libreforge.effects.impl.EffectSpawnParticle
import com.willfp.libreforge.effects.impl.EffectSpawnPotionCloud
import com.willfp.libreforge.effects.impl.EffectStrikeLightning
import com.willfp.libreforge.effects.impl.EffectStripAI
import com.willfp.libreforge.effects.impl.EffectSwarm
import com.willfp.libreforge.effects.impl.EffectTakeMoney
import com.willfp.libreforge.effects.impl.EffectTargetPlayer
import com.willfp.libreforge.effects.impl.EffectTelekinesis
import com.willfp.libreforge.effects.impl.EffectTeleport
import com.willfp.libreforge.effects.impl.EffectTeleportTo
import com.willfp.libreforge.effects.impl.EffectTeleportToGround
import com.willfp.libreforge.effects.impl.EffectTotalDamageMultiplier
import com.willfp.libreforge.effects.impl.EffectTraceback
import com.willfp.libreforge.effects.impl.EffectTransmission
import com.willfp.libreforge.effects.impl.EffectTriggerCustom
import com.willfp.libreforge.effects.impl.EffectTriggerNestedChain
import com.willfp.libreforge.effects.impl.EffectUnderwaterMiningSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectUpdateBossBar
import com.willfp.libreforge.effects.impl.EffectVictimSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectVillagerTradeMultiplier
import com.willfp.libreforge.effects.impl.EffectWaterMovementEfficiencyMultiplier
import com.willfp.libreforge.effects.impl.EffectXpMultiplier
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.paper.impl.EffectDropPickupItem
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.separatorAmbivalent
import com.willfp.libreforge.toWeightedList
import com.willfp.libreforge.triggers.Triggers
import java.util.UUID

object Effects : Registry<Effect<*>>() {
    private val identifiedChains = mutableMapOf<String, Chain>()

    /**
     * Get a chain by [id].
     */
    fun getChainByID(id: String): Chain? {
        return identifiedChains[id]
    }

    /**
     * Register a new [chain] with a certain [id].
     */
    fun register(id: String, chain: Chain) {
        identifiedChains[id] = chain
    }

    /**
     * Compile a list of [configs] into an EffectList in a given [context].
     */
    fun compile(configs: Collection<Config>, context: ViolationContext): EffectList =
        EffectList(configs.mapNotNull { compile(it, context) })

    /**
     * Compile a [cfg] into an EffectBlock in a given [context].
     */
    fun compile(cfg: Config, context: ViolationContext): EffectBlock? {
        val config = cfg.separatorAmbivalent()

        val args = config.getSubsection("args")

        val (arguments, conditions, mutators, filters) = compileEffectContext(config, context)

        val triggers = config.getStrings("triggers").mapNotNull {
            Triggers[it]
        }.toSet()

        val effectConfigs = if (config.has("id")) {
            listOf(config)
        } else {
            config.getSubsections("effects")
        }

        val directIDSpecified = config.has("id")

        val executor = ChainExecutors.getByID(args.getStringOrNull("run-type"))

        val chain = compileChain(effectConfigs, executor, context, directIDSpecified) ?: return null

        val permanentEffects = chain.filter { it.effect.isPermanent }
        val triggeredEffects = chain.filterNot { it.effect.isPermanent }

        if (triggers.isNotEmpty() && permanentEffects.isNotEmpty()) {
            context.log(
                ConfigViolation(
                    "triggers", "Triggers are not allowed on permanent " +
                            "effects: ${permanentEffects.joinToString(", ") { it.effect.id }}!"
                )
            )
            return null
        }

        if (triggers.isEmpty() && chain.any { !it.effect.isPermanent }) {
            context.log(
                ConfigViolation(
                    "triggers", "You must specify at least one trigger for " +
                            "triggered effects: ${triggeredEffects.joinToString(", ") { it.effect.id }}!"
                )
            )
            return null
        }

        var isInvalid = false
        for (element in chain) {
            for (trigger in triggers) {
                if (!element.effect.supportsTrigger(trigger, mutators)) {
                    isInvalid = true
                    context.log(
                        ConfigViolation(
                            "triggers",
                            "${element.effect.id} does not support trigger ${trigger.id}"
                        )
                    )
                }

                if (trigger.deprecationMessage != null) {
                    context.log(
                        ConfigWarning(
                            "triggers",
                            "Trigger ${trigger.id} is deprecated: ${trigger.deprecationMessage}. It will be removed in the future."
                        )
                    )
                }
            }

            if (!triggers.all { element.effect.supportsTrigger(it) }) {
                isInvalid = true
            }
        }

        if (isInvalid) {
            return null
        }

        return EffectBlock(
            UUID.randomUUID(),
            args,
            chain,
            triggers,
            arguments,
            conditions,
            mutators,
            filters,
            directIDSpecified
        )
    }

    /**
     * Compile a list of [configs] into a Chain in a given [context] with a normal executor.
     */
    fun compileChain(
        configs: Collection<Config>,
        context: ViolationContext
    ) = compileChain(configs, NormalExecutorFactory.create(), context)

    /**
     * Compile a list of [configs] and an [executor] into a Chain in a given [context].
     */
    fun compileChain(
        configs: Collection<Config>,
        executor: ChainExecutor,
        context: ViolationContext,
    ): Chain? = compileChain(configs, executor, context, false)

    /**
     * Compile a [config] into a Rich Chain in a given [context].
     */
    fun compileRichChain(
        config: Config,
        context: ViolationContext
    ): RichChain? {
        val args = config.getSubsection("args")

        val chain = compileChain(
            config.getSubsections("effects"),
            ChainExecutors.getByID(args.getStringOrNull("run-type")),
            context.with("effects")
        ) ?: return null

        val (arguments, conditions, mutators, filters) = compileEffectContext(config, context)

        return RichChain(
            UUID.randomUUID(),
            args,
            chain,
            arguments,
            conditions,
            mutators,
            filters,
        )
    }

    private fun compileEffectContext(
        config: Config,
        context: ViolationContext
    ): EffectContext {
        val arguments = EffectArguments.compile(config.getSubsection("args"), context.with("args"))
        val conditions = Conditions.compile(config.getSubsections("conditions"), context.with("conditions"))
        val mutators = Mutators.compile(config.getSubsections("mutators"), context.with("mutators"))
        val filters = Filters.compile(config.getSubsection("filters"), context.with("filters"))

        return EffectContext(
            arguments,
            conditions,
            mutators,
            filters
        )
    }

    private fun compileChain(
        configs: Collection<Config>,
        executor: ChainExecutor,
        context: ViolationContext,
        directIDSpecified: Boolean // If it's configured with 'id', rather than 'effects'
    ): Chain? {
        val elements = configs
            .map { it.separatorAmbivalent() }
            .mapNotNull { compileElement(it, context) }
            .toWeightedList()

        if ((elements.size > 1 || !directIDSpecified) && elements.any { it.effect.isPermanent }) {
            context.log(
                ConfigViolation(
                    "effects",
                    "Permanent effects (${
                        elements.filter { it.effect.isPermanent }.joinToString(", ") { it.effect.id }
                    }) are not allowed in chains!")
            )
            return null
        }

        return Chain(elements, executor)
    }

    private fun compileElement(config: Config, context: ViolationContext): ChainElement<*>? {
        if (config.has("effects")) {
            return compileNestedChain(config, context)
        }

        val id = config.getString("id")
        val effect = this.get(id)

        if (effect == null) {
            context.log(ConfigViolation("id", "Invalid effect ID specified: ${id}!"))
            return null
        }

        if (effect.deprecationMessage != null) {
            context.log(
                ConfigWarning(
                    "id",
                    "Effect $id is deprecated: ${effect.deprecationMessage}. It will be removed in the future."
                )
            )
        }

        return makeElement(effect, config, context)
    }

    private fun compileNestedChain(config: Config, context: ViolationContext): ChainElement<RichChain?>? {
        val compileData = EffectTriggerNestedChain.makeCompileData(config, context)

        return makeElement(
            EffectTriggerNestedChain,
            config,
            context,
            forceCompileData = compileData
        )
    }

    private fun <T> makeElement(
        effect: Effect<T>,
        config: Config,
        context: ViolationContext,

        // For nested chains
        forceCompileData: T? = null
    ): ChainElement<T>? {
        val args = config.getSubsection("args")

        if (!effect.checkConfig(args, context.with("args"))) {
            return null
        }

        val compileData = forceCompileData ?: effect.makeCompileData(args, context.with("args"))

        val (arguments, conditions, mutators, filters) = compileEffectContext(config, context)

        val weight = config.getDoubleFromExpression("weight")

        val forceRunOrder = if (args.has("run_order")) {
            enumValueOfOrNull<RunOrder>(args.getString("run_order").uppercase())
        } else null

        return ChainElement(
            effect,
            args,
            compileData,
            arguments,
            conditions,
            mutators,
            filters,
            weight,
            forceRunOrder
        )
    }

    init {
        register(EffectAOE)
        register(EffectAOEBlocks)
        register(EffectAddDamage)
        register(EffectAddEnchant)
        register(EffectAddGlobalPoints)
        register(EffectAddHolder)
        register(EffectAddHolderInRadius)
        register(EffectAddHolderToVictim)
        register(EffectAddPermanentHolderInRadius)
        register(EffectAddPoints)
        register(EffectAgeCrop)
        register(EffectAllPlayers)
        register(EffectAnimation)
        register(EffectArmor)
        register(EffectArmorToughness)
        register(EffectArrowRing)
        register(EffectAttackSpeedMultiplier)
        register(EffectAutosmelt)
        register(EffectBleed)
        register(EffectBlockCommands)
        register(EffectBonusHealth)
        register(EffectBreakBlock)
        register(EffectBrewTimeMultiplier)
        register(EffectBroadcast)
        register(EffectCancelEvent)
        register(EffectClearInvulnerability)
        register(EffectCloseInventory)
        register(EffectConsumeHeldItem)
        register(EffectCreateBossBar)
        register(EffectCreateExplosion)
        register(EffectCreateHologram)
        register(EffectCritMultiplier)
        register(EffectDamageArmor)
        register(EffectDamageItem)
        register(EffectDamageMainhand)
        register(EffectDamageMultiplier)
        register(EffectDamageNearbyEntities)
        register(EffectDamageOffhand)
        register(EffectDamageTwice)
        register(EffectDamageVictim)
        register(EffectDontConsumeLapisChance)
        register(EffectDontConsumeXpChance)
        register(EffectDrill)
        register(EffectDropItem)
        register(EffectDropItemForPlayer)
        register(EffectDropItemSlot)
        register(EffectDropPickupItem)
        register(EffectDropRandomItem)
        register(EffectDropRandomItemForPlayer)
        register(EffectDropWeightedRandomItem)
        register(EffectExtinguish)
        register(EffectFeatherStep)
        register(EffectFlight)
        register(EffectFoodMultiplier)
        register(EffectGiveFood)
        register(EffectGiveGlobalPoints)
        register(EffectGiveHealth)
        register(EffectGiveItem)
        register(EffectGiveItemPoints)
        register(EffectGiveMoney)
        register(EffectGiveOxygen)
        register(EffectGivePoints)
        register(EffectGivePrice)
        register(EffectGiveSaturation)
        register(EffectGiveXp)
        register(EffectGlowNearbyBlocks)
        register(EffectHoming)
        register(EffectHungerMultiplier)
        register(EffectIgnite)
        register(EffectItemDurabilityMultiplier)
        register(EffectKeepInventory)
        register(EffectKeepLevel)
        register(EffectKick)
        register(EffectKnockAway)
        register(EffectKnockbackMultiplier)
        register(EffectKnockbackResistanceMultiplier)
        register(EffectLevelItem)
        register(EffectLuckMultiplier)
        register(EffectMineRadius)
        register(EffectMineRadiusOneDeep)
        register(EffectMineVein)
        register(EffectMovementSpeedMultiplier)
        register(EffectMultiplyDrops)
        register(EffectMultiplyGlobalPoints)
        register(EffectMultiplyItemPoints)
        register(EffectMultiplyPoints)
        register(EffectMultiplyVelocity)
        register(EffectNameEntity)
        register(EffectOpenCrafting)
        register(EffectOpenEnderChest)
        register(EffectParticleAnimation)
        register(EffectParticleLine)
        register(EffectPayPrice)
        register(EffectPermanentPotionEffect)
        register(EffectPiercing)
        register(EffectPlaySound)
        register(EffectPotionDurationMultiplier)
        register(EffectPotionEffect)
        register(EffectPullIn)
        register(EffectPullToLocation)
        register(EffectRandomPlayer)
        register(EffectRapidBows)
        register(EffectReelSpeedMultiplier)
        register(EffectRegenMultiplier)
        register(EffectRemoveBossBar)
        register(EffectRemoveEnchant)
        register(EffectRemoveItem)
        register(EffectRemoveItemData)
        register(EffectRemovePotionEffect)
        register(EffectReplaceNear)
        register(EffectReplantCrops)
        register(EffectRepairItem)
        register(EffectRotate)
        register(EffectRotateVictim)
        register(EffectRunChain)
        register(EffectRunChainInline)
        register(EffectRunCommand)
        register(EffectRunPlayerCommand)
        register(EffectSellItems)
        register(EffectSellMultiplier)
        register(EffectSendMessage)
        register(EffectSendTitle)
        register(EffectSetArmorTrim)
        register(EffectSetBlock)
        register(EffectSetCustomModelData)
        register(EffectSetFood)
        register(EffectSetFreezeTicks)
        register(EffectSetGlobalPoints)
        register(EffectSetItemData)
        register(EffectSetItemPoints)
        register(EffectSetPoints)
        register(EffectSetSaturation)
        register(EffectSetVelocity)
        register(EffectSetVictimVelocity)
        register(EffectShoot)
        register(EffectShootArrow)
        register(EffectShuffleHotbar)
        register(EffectSmite)
        register(EffectSpawnEntity)
        register(EffectSpawnMobs)
        register(EffectSpawnParticle)
        register(EffectSpawnPotionCloud)
        register(EffectStrikeLightning)
        register(EffectStripAI)
        register(EffectSwarm)
        register(EffectTakeMoney)
        register(EffectTargetPlayer)
        register(EffectTelekinesis)
        register(EffectTeleport)
        register(EffectTeleportTo)
        register(EffectTeleportToGround)
        register(EffectTotalDamageMultiplier)
        register(EffectTraceback)
        register(EffectTransmission)
        register(EffectTriggerCustom)
        register(EffectUpdateBossBar)
        register(EffectVictimSpeedMultiplier)
        register(EffectVillagerTradeMultiplier)
        register(EffectXpMultiplier)
        register(EffectBlockReach)
        register(EffectMiningEfficiency)
        register(EffectEntityReach)
        register(EffectGravityMultiplier)
        register(EffectIncreaseStepHeight)
        register(EffectJumpStrengthMultiplier)
        register(EffectMiningSpeedMultiplier)
        register(EffectMovementEfficiencyMultiplier)
        register(EffectSneakingSpeedMultiplier)
        register(EffectUnderwaterMiningSpeedMultiplier)
        register(EffectSafeFallDistance)
        register(EffectAddDurability)
        register(EffectScale)
        register(EffectWaterMovementEfficiencyMultiplier)
        register(EffectBurningTimeMultiplier)
        register(EffectExplosionKnockbackResistanceMultiplier)
    }
}
