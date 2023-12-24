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
import com.willfp.libreforge.effects.impl.*
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
        register(EffectAddDamage)
        register(EffectAddHolder)
        register(EffectAddHolderInRadius)
        register(EffectAddHolderToVictim)
        register(EffectAddPermanentHolderInRadius)
        register(EffectAddPoints)
        register(EffectAOE)
        register(EffectArmor)
        register(EffectArmorToughness)
        register(EffectArrowRing)
        register(EffectAttackSpeedMultiplier)
        register(EffectAutosmelt)
        register(EffectBleed)
        register(EffectBlockCommands)
        register(EffectBonusHealth)
        register(EffectBreakBlock)
        register(EffectBroadcast)
        register(EffectCancelEvent)
        register(EffectConsumeHeldItem)
        register(EffectCreateExplosion)
        register(EffectCritMultiplier)
        register(EffectDamageArmor)
        register(EffectDamageMainhand)
        register(EffectDamageMultiplier)
        register(EffectDamageNearbyEntities)
        register(EffectDamageVictim)
        register(EffectDrill)
        register(EffectDropItem)
        register(EffectDropItemForPlayer)
        register(EffectExtinguish)
        register(EffectFeatherStep)
        register(EffectFlight)
        register(EffectFoodMultiplier)
        register(EffectGiveFood)
        register(EffectGiveHealth)
        register(EffectGiveItem)
        register(EffectGiveMoney)
        register(EffectGiveOxygen)
        register(EffectGivePoints)
        register(EffectGivePrice)
        register(EffectGiveXp)
        register(EffectGlowNearbyBlocks)
        register(EffectHungerMultiplier)
        register(EffectIgnite)
        register(EffectKeepInventory)
        register(EffectKnockAway)
        register(EffectKnockbackMultiplier)
        register(EffectKnockbackResistanceMultiplier)
        register(EffectLuckMultiplier)
        register(EffectMineRadius)
        register(EffectMineRadiusOneDeep)
        register(EffectMineVein)
        register(EffectMovementSpeedMultiplier)
        register(EffectMultiplyDrops)
        register(EffectMultiplyPoints)
        register(EffectMultiplyVelocity)
        register(EffectParticleAnimation)
        register(EffectParticleLine)
        register(EffectPayPrice)
        register(EffectPermanentPotionEffect)
        register(EffectPlaySound)
        register(EffectPotionEffect)
        register(EffectPullIn)
        register(EffectPullToLocation)
        register(EffectRegenMultiplier)
        register(EffectRemoveItem)
        register(EffectRemovePotionEffect)
        register(EffectRotate)
        register(EffectRunChain)
        register(EffectRunChainInline)
        register(EffectRunCommand)
        register(EffectRunPlayerCommand)
        register(EffectSellItems)
        register(EffectSellMultiplier)
        register(EffectSendMessage)
        register(EffectSendTitle)
        register(EffectSetBlock)
        register(EffectSetFreezeTicks)
        register(EffectSetPoints)
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
        register(EffectTakeMoney)
        register(EffectTeleport)
        register(EffectTeleportTo)
        register(EffectTeleportToGround)
        register(EffectTraceback)
        register(EffectTransmission)
        register(EffectTriggerCustom)
        register(EffectXpMultiplier)
        register(EffectItemDurabilityMultiplier)
        register(EffectKick)
        register(EffectCreateHologram)
        register(EffectClearInvulnerability)
        register(EffectHoming)
        register(EffectPiercing)
        register(EffectDamageTwice)
        register(EffectDamageItem)
        register(EffectRepairItem)
        register(EffectBrewTimeMultiplier)
        register(EffectPotionDurationMultiplier)
        register(EffectDontConsumeLapisChance)
        register(EffectDontConsumeXpChance)
        register(EffectGiveGlobalPoints)
        register(EffectMultiplyGlobalPoints)
        register(EffectReelSpeedMultiplier)
        register(EffectDropRandomItem)
        register(EffectDropRandomItemForPlayer)
        register(EffectDropPickupItem)
        register(EffectOpenEnderChest)
        register(EffectAddEnchant)
        register(EffectRemoveEnchant)
        register(EffectAllPlayers)
        register(EffectRandomPlayer)
        register(EffectAgeCrop)
        register(EffectGiveItemPoints)
        register(EffectLevelItem)
        register(EffectMultiplyItemPoints)
        register(EffectSetGlobalPoints)
        register(EffectSetItemPoints)
        register(EffectSetCustomModelData)
        register(EffectDropWeightedRandomItem)
        register(EffectReplantCrops)
        register(EffectSwarm)
        register(EffectTargetPlayer)
        register(EffectReplaceNear)
        register(EffectCloseInventory)
        register(EffectTelekinesis)
        register(EffectRapidBows)
        register(EffectNameEntity)
        register(EffectTotalDamageMultiplier)
        register(EffectVictimSpeedMultiplier)
        register(EffectSetArmorTrim)
        register(EffectSetItemData)
        register(EffectRemoveItemData)
        register(EffectAOEBlocks)
        register(EffectGiveSaturation)
        register(EffectSetSaturation)
        register(EffectSetFood)
        register(EffectDamageOffhand)
        register(EffectCreateBossBar)
        register(EffectRemoveBossBar)
        register(EffectUpdateBossBar)
        register(EffectOpenCrafting)
        register(EffectAddGlobalPoints)
        register(EffectDropItemSlot)
        register(EffectKeepLevel)
    }
}
