package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.arguments.EffectArguments
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.effects.impl.EffectAOE
import com.willfp.libreforge.effects.impl.EffectAddDamage
import com.willfp.libreforge.effects.impl.EffectAddHolder
import com.willfp.libreforge.effects.impl.EffectAddHolderInRadius
import com.willfp.libreforge.effects.impl.EffectAddHolderToVictim
import com.willfp.libreforge.effects.impl.EffectAddPermanentHolderInRadius
import com.willfp.libreforge.effects.impl.EffectAddPoints
import com.willfp.libreforge.effects.impl.EffectArmor
import com.willfp.libreforge.effects.impl.EffectArmorToughness
import com.willfp.libreforge.effects.impl.EffectArrowRing
import com.willfp.libreforge.effects.impl.EffectAttackSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectAutosmelt
import com.willfp.libreforge.effects.impl.EffectBleed
import com.willfp.libreforge.effects.impl.EffectBlockCommands
import com.willfp.libreforge.effects.impl.EffectBonusHealth
import com.willfp.libreforge.effects.impl.EffectBreakBlock
import com.willfp.libreforge.effects.impl.EffectBroadcast
import com.willfp.libreforge.effects.impl.EffectCancelEvent
import com.willfp.libreforge.effects.impl.EffectConsumeHeldItem
import com.willfp.libreforge.effects.impl.EffectCreateExplosion
import com.willfp.libreforge.effects.impl.EffectCritMultiplier
import com.willfp.libreforge.effects.impl.EffectDamageArmor
import com.willfp.libreforge.effects.impl.EffectDamageMainhand
import com.willfp.libreforge.effects.impl.EffectDamageNearbyEntities
import com.willfp.libreforge.effects.impl.EffectDamageVictim
import com.willfp.libreforge.effects.impl.EffectDrill
import com.willfp.libreforge.effects.impl.EffectDropItem
import com.willfp.libreforge.effects.impl.EffectDropItemForPlayer
import com.willfp.libreforge.effects.impl.EffectExtinguish
import com.willfp.libreforge.effects.impl.EffectFeatherStep
import com.willfp.libreforge.effects.impl.EffectFlight
import com.willfp.libreforge.effects.impl.EffectFoodMultiplier
import com.willfp.libreforge.effects.impl.EffectGiveFood
import com.willfp.libreforge.effects.impl.EffectGiveHealth
import com.willfp.libreforge.effects.impl.EffectGiveItem
import com.willfp.libreforge.effects.impl.EffectGiveMoney
import com.willfp.libreforge.effects.impl.EffectGiveOxygen
import com.willfp.libreforge.effects.impl.EffectGivePoints
import com.willfp.libreforge.effects.impl.EffectGivePrice
import com.willfp.libreforge.effects.impl.EffectGiveXp
import com.willfp.libreforge.effects.impl.EffectGlowNearbyBlocks
import com.willfp.libreforge.effects.impl.EffectHungerMultiplier
import com.willfp.libreforge.effects.impl.EffectIgnite
import com.willfp.libreforge.effects.impl.EffectKeepInventory
import com.willfp.libreforge.effects.impl.EffectKnockAway
import com.willfp.libreforge.effects.impl.EffectKnockbackMultiplier
import com.willfp.libreforge.effects.impl.EffectKnockbackResistanceMultiplier
import com.willfp.libreforge.effects.impl.EffectLuckMultiplier
import com.willfp.libreforge.effects.impl.EffectMineRadius
import com.willfp.libreforge.effects.impl.EffectMineRadiusOneDeep
import com.willfp.libreforge.effects.impl.EffectMineVein
import com.willfp.libreforge.effects.impl.EffectMovementSpeedMultiplier
import com.willfp.libreforge.effects.impl.EffectMultiplyDrops
import com.willfp.libreforge.effects.impl.EffectMultiplyPoints
import com.willfp.libreforge.effects.impl.EffectMultiplyVelocity
import com.willfp.libreforge.effects.impl.EffectParticleAnimation
import com.willfp.libreforge.effects.impl.EffectParticleLine
import com.willfp.libreforge.effects.impl.EffectPayPrice
import com.willfp.libreforge.effects.impl.EffectPermanentPotionEffect
import com.willfp.libreforge.effects.impl.EffectPlaySound
import com.willfp.libreforge.effects.impl.EffectPotionEffect
import com.willfp.libreforge.effects.impl.EffectPullIn
import com.willfp.libreforge.effects.impl.EffectPullToLocation
import com.willfp.libreforge.effects.impl.EffectRegenMultiplier
import com.willfp.libreforge.effects.impl.EffectRemoveItem
import com.willfp.libreforge.effects.impl.EffectRemovePotionEffect
import com.willfp.libreforge.effects.impl.EffectRotate
import com.willfp.libreforge.effects.impl.EffectRunChain
import com.willfp.libreforge.effects.impl.EffectRunChainInline
import com.willfp.libreforge.effects.impl.EffectRunCommand
import com.willfp.libreforge.effects.impl.EffectRunPlayerCommand
import com.willfp.libreforge.effects.impl.EffectSellItems
import com.willfp.libreforge.effects.impl.EffectSellMultiplier
import com.willfp.libreforge.effects.impl.EffectSendMessage
import com.willfp.libreforge.effects.impl.EffectSendTitle
import com.willfp.libreforge.effects.impl.EffectSetBlock
import com.willfp.libreforge.effects.impl.EffectSetFreezeTicks
import com.willfp.libreforge.effects.impl.EffectSetPoints
import com.willfp.libreforge.effects.impl.EffectSetVelocity
import com.willfp.libreforge.effects.impl.EffectSetVictimVelocity
import com.willfp.libreforge.effects.impl.EffectShoot
import com.willfp.libreforge.effects.impl.EffectShootArrow
import com.willfp.libreforge.effects.impl.EffectShuffleHotbar
import com.willfp.libreforge.effects.impl.EffectSmite
import com.willfp.libreforge.effects.impl.EffectSpawnEntity
import com.willfp.libreforge.effects.impl.EffectSpawnMobs
import com.willfp.libreforge.effects.impl.EffectSpawnParticle
import com.willfp.libreforge.effects.impl.EffectSpawnPotionCloud
import com.willfp.libreforge.effects.impl.EffectStrikeLightning
import com.willfp.libreforge.effects.impl.EffectStripAI
import com.willfp.libreforge.effects.impl.EffectTakeMoney
import com.willfp.libreforge.effects.impl.EffectTeleport
import com.willfp.libreforge.effects.impl.EffectTeleportTo
import com.willfp.libreforge.effects.impl.EffectTeleportToGround
import com.willfp.libreforge.effects.impl.EffectTraceback
import com.willfp.libreforge.effects.impl.EffectTransmission
import com.willfp.libreforge.effects.impl.EffectTriggerCustom
import com.willfp.libreforge.effects.impl.EffectXpMultiplier
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.separatorAmbivalent
import com.willfp.libreforge.triggers.Triggers
import java.util.UUID

object Effects {
    private val registry = mutableMapOf<String, Effect<*>>()
    private val identifiedChains = mutableMapOf<String, Chain>()

    /**
     * Get an effect by [id].
     */
    fun getByID(id: String): Effect<*>? {
        return registry[id]
    }

    /**
     * Get a chain by [id].
     */
    fun getChainByID(id: String): Chain? {
        return identifiedChains[id]
    }

    /**
     * Register a new [effect].
     */
    fun register(effect: Effect<*>) {
        registry[effect.id] = effect
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

        val arguments = EffectArguments.compile(args, context.with("args"))
        val conditions = Conditions.compile(config.getSubsections("conditions"), context.with("conditions"))
        val mutators = Mutators.compile(config.getSubsections("mutators"), context.with("mutators"))
        val filters = Filters.compile(config.getSubsection("filters"), context.with("filters"))
        val triggers = config.getStrings("triggers").mapNotNull {
            Triggers.getByID(it)
        }

        val effectConfigs = if (config.has("id")) {
            listOf(config)
        } else {
            config.getSubsections("effects")
        }

        val directIDSpecified = config.has("id")

        val executor = ChainExecutors.getByID(args.getString("run-type"))

        if (executor == null) {
            context.with("args")
                .log(ConfigViolation("run-type", "Invalid run type specified!"))
            return null
        }

        val chain = compileChain(effectConfigs, executor, context, directIDSpecified) ?: return null

        if (triggers.isNotEmpty() && chain.any { it.effect.isPermanent }) {
            context.log(ConfigViolation("triggers", "Triggers are not allowed on permanent effects!"))
            return null
        }

        if (triggers.isEmpty() && chain.any { !it.effect.isPermanent }) {
            context.log(ConfigViolation("triggers", "You must specify at least one trigger!"))
            return null
        }

        var isInvalid = false
        for (element in chain) {
            for (trigger in triggers) {
                if (!element.effect.supportsTrigger(trigger)) {
                    isInvalid = true
                    context.log(
                        ConfigViolation(
                            "triggers",
                            "${element.effect.id} does not support trigger ${trigger.id}"
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
            filters
        )
    }

    /**
     * Compile a list of [configs] and an [executor] into a Chain in a given [context].
     */
    fun compileChain(
        configs: Collection<Config>,
        executor: ChainExecutor,
        context: ViolationContext,
    ): Chain? = compileChain(configs, executor, context, false)

    private fun compileChain(
        configs: Collection<Config>,
        executor: ChainExecutor,
        context: ViolationContext,
        directIDSpecified: Boolean // If it's configured with 'id', rather than 'effects'
    ): Chain? {
        val elements = configs.map { it.separatorAmbivalent() }.mapNotNull { compileElement(it, context) }

        if ((elements.size > 1 || !directIDSpecified) && elements.any { it.effect.isPermanent }) {
            context.log(ConfigViolation("effects", "Permanent effects are not allowed in chains!"))
            return null
        }

        return Chain(elements, executor)
    }

    private fun compileElement(config: Config, context: ViolationContext): ChainElement<*>? {
        val effect = getByID(config.getString("id"))

        if (effect == null) {
            context.log(ConfigViolation("id", "Invalid effect ID specified!"))
            return null
        }

        return makeElement(effect, config, context)
    }

    private fun <T> makeElement(
        effect: Effect<T>,
        config: Config,
        context: ViolationContext
    ): ChainElement<T>? {
        val args = config.getSubsection("args")

        if (!effect.checkConfig(args, context.with("args"))) {
            return null
        }

        val compileData = effect.makeCompileData(args, context.with("args"))

        val arguments = EffectArguments.compile(args, context.with("args"))
        val conditions = Conditions.compile(config.getSubsections("conditions"), context.with("conditions"))
        val mutators = Mutators.compile(config.getSubsections("mutators"), context.with("mutators"))
        val filters = Filters.compile(config.getSubsection("filters"), context.with("filters"))

        return ChainElement(
            effect,
            args,
            compileData,
            arguments,
            conditions,
            mutators,
            filters
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
    }
}
