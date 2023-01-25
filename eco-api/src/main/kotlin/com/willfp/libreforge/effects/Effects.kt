package com.willfp.libreforge.effects

import com.google.common.collect.HashBiMap
import com.willfp.eco.core.config.emptyConfig
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.chains.EffectChains
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.arguments.EffectArgumentChance
import com.willfp.libreforge.effects.arguments.EffectArgumentCooldown
import com.willfp.libreforge.effects.arguments.EffectArgumentCost
import com.willfp.libreforge.effects.arguments.EffectArgumentEvery
import com.willfp.libreforge.effects.arguments.EffectArgumentPointCost
import com.willfp.libreforge.effects.arguments.EffectArgumentPrice
import com.willfp.libreforge.effects.arguments.EffectArgumentRequire
import com.willfp.libreforge.effects.effects.*
import com.willfp.libreforge.separatorAmbivalent
import com.willfp.libreforge.triggers.DataMutators
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.Triggers
import java.util.UUID

@Suppress("UNUSED")
object Effects {
    private val BY_ID = HashBiMap.create<String, Effect>()
    private val EFFECT_ARGUMENTS = mutableSetOf<EffectArgument>()

    val DAMAGE_MULTIPLIER: Effect = EffectDamageMultiplier()
    val CRIT_MULTIPLIER: Effect = EffectCritMultiplier()
    val KNOCKBACK_MULTIPLIER: Effect = EffectKnockbackMultiplier()
    val GIVE_MONEY: Effect = EffectGiveMoney()
    val ATTACK_SPEED_MULTIPLIER: Effect = EffectAttackSpeedMultiplier()
    val MOVEMENT_SPEED_MULTIPLIER: Effect = EffectMovementSpeedMultiplier()
    val BONUS_HEALTH: Effect = EffectBonusHealth()
    val RUN_COMMAND: Effect = EffectRunCommand()
    val STRIKE_LIGHTNING: Effect = EffectStrikeLightning()
    val SPAWN_MOBS: Effect = EffectSpawnMobs()
    val HUNGER_MULTIPLIER: Effect = EffectHungerMultiplier()
    val REGEN_MULTIPLIER: Effect = EffectRegenMultiplier()
    val PERMANENT_POTION_EFFECT: Effect = EffectPermanentPotionEffect()
    val POTION_EFFECT: Effect = EffectPotionEffect()
    val ARMOR: Effect = EffectArmor()
    val ARMOR_TOUGHNESS: Effect = EffectArmorToughness()
    val GIVE_XP: Effect = EffectGiveXp()
    val XP_MULTIPLIER: Effect = EffectXpMultiplier()
    val BLEED: Effect = EffectBleed()
    val ARROW_RING: Effect = EffectArrowRing()
    val FOOD_MULTIPLIER: Effect = EffectFoodMultiplier()
    val AUTOSMELT: Effect = EffectAutosmelt()
    val TELEPORT: Effect = EffectTeleport()
    val CANCEL_EVENT: Effect = EffectCancelEvent()
    val SEND_MESSAGE: Effect = EffectSendMessage()
    val GIVE_FOOD: Effect = EffectGiveFood()
    val GIVE_HEALTH: Effect = EffectGiveHealth()
    val BREAK_BLOCK: Effect = EffectBreakBlock()
    val REMOVE_POTION_EFFECT: Effect = EffectRemovePotionEffect()
    val PLAY_SOUND: Effect = EffectPlaySound()
    val IGNITE: Effect = EffectIgnite()
    val FEATHER_STEP: Effect = EffectFeatherStep()
    val MINE_RADIUS: Effect = EffectMineRadius()
    val GIVE_POINTS: Effect = EffectGivePoints()
    val SET_POINTS: Effect = EffectSetPoints()
    val MULTIPLY_POINTS: Effect = EffectMultiplyPoints()
    val MULTIPLY_DROPS: Effect = EffectMultiplyDrops()
    val SPAWN_PARTICLE: Effect = EffectSpawnParticle()
    val PULL_TO_LOCATION: Effect = EffectPullToLocation()
    val DAMAGE_ARMOR: Effect = EffectDamageArmor()
    val EXTINGUISH: Effect = EffectExtinguish()
    val GIVE_OXYGEN: Effect = EffectGiveOxygen()
    val RUN_PLAYER_COMMAND: Effect = EffectRunPlayerCommand()
    val DRILL: Effect = EffectDrill()
    val DAMAGE_NEARBY_ENTITIES: Effect = EffectDamageNearbyEntities()
    val SEND_TITLE: Effect = EffectSendTitle()
    val RUN_CHAIN: Effect = EffectRunChain()
    val DAMAGE_VICTIM: Effect = EffectDamageVictim()
    val RUN_CHAIN_INLINE: EffectRunChainInline = EffectRunChainInline() // Explicit type for compile data
    val SELL_MULTIPLIER: EffectSellMultiplier = EffectSellMultiplier() // Explicit type for hooks
    val BLOCK_COMMANDS: Effect = EffectBlockCommands()
    val GIVE_ITEM: Effect = EffectGiveItem()
    val MULTIPLY_VELOCITY: Effect = EffectMultiplyVelocity()
    val SHOOT_ARROW: Effect = EffectShootArrow()
    val STRIP_AI: Effect = EffectStripAI()
    val TRANSMISSION: Effect = EffectTransmission()
    val SET_VELOCITY: Effect = EffectSetVelocity()
    val PARTICLE_LINE: Effect = EffectParticleLine()
    val PARTICLE_ANIMATION: Effect = EffectParticleAnimation()
    val KEEP_INVENTORY: Effect = EffectKeepInventory()
    val REMOVE_ITEM: Effect = EffectRemoveItem()
    val MINE_RADIUS_ONE_DEEP: Effect = EffectMineRadiusOneDeep()
    val SET_FREEZE_TICKS: Effect = EffectSetFreezeTicks()
    val SHOOT: Effect = EffectShoot()
    val LUCK_MULTIPLIER: Effect = EffectLuckMultiplier()
    val ADD_POINTS: Effect = EffectAddPoints()
    val ADD_HOLDER: Effect = EffectAddHolder()
    val SPAWN_POTION_CLOUD: Effect = EffectSpawnPotionCloud()
    val CREATE_EXPLOSION: Effect = EffectCreateExplosion()
    val TRACEBACK: EffectTraceback = EffectTraceback() // Explicit type for init()
    val ROTATE: Effect = EffectRotate()
    val CONSUME_HELD_ITEM: Effect = EffectConsumeHeldItem()
    val TELEPORT_TO: Effect = EffectTeleportTo()
    val TRIGGER_CUSTOM: Effect = EffectTriggerCustom()
    val KNOCKBACK_RESISTANCE_MULTIPLIER: Effect = EffectKnockbackResistanceMultiplier()
    val SMITE: Effect = EffectSmite()
    val SHUFFLE_HOTBAR: Effect = EffectShuffleHotbar()
    val BROADCAST: Effect = EffectBroadcast()
    val GLOW_NEARBY_BLOCKS: Effect = EffectGlowNearbyBlocks()
    val MINE_VEIN: Effect = EffectMineVein()
    val SET_BLOCK: Effect = EffectSetBlock()
    val SELL_ITEMS: Effect = EffectSellItems()
    val ADD_HOLDER_IN_RADIUS: Effect = EffectAddHolderInRadius()
    val ADD_PERMANENT_HOLDER_IN_RADIUS: Effect = EffectAddPermanentHolderInRadius()
    val ADD_DAMAGE: Effect = EffectAddDamage()
    val TAKE_MONEY: Effect = EffectTakeMoney()
    val SPAWN_ENTITY: Effect = EffectSpawnEntity()
    val KNOCK_AWAY: Effect = EffectKnockAway()
    val PULL_IN: Effect = EffectPullIn()
    val TELEPORT_TO_GROUND: Effect = EffectTeleportToGround()
    val DROP_ITEM: Effect = EffectDropItem()
    val DROP_ITEM_FOR_PLAYER: Effect = EffectDropItemForPlayer()
    val AOE: Effect = EffectAOE()
    val SET_VICTIM_VELOCITY: Effect = EffectSetVictimVelocity()
    val DAMAGE_MAINHAND: Effect = EffectDamageMainhand()
    val ADD_HOLDER_TO_VICTIM: Effect = EffectAddHolderToVictim()
    val GIVE_PRICE: Effect = EffectGivePrice()
    val PAY_PRICE: Effect = EffectPayPrice()
    val FLIGHT: Effect = EffectFlight()

    init {
        addNewEffectArgument(EffectArgumentChance)
        addNewEffectArgument(EffectArgumentCooldown)
        addNewEffectArgument(EffectArgumentCost)
        addNewEffectArgument(EffectArgumentEvery)
        addNewEffectArgument(EffectArgumentPointCost)
        addNewEffectArgument(EffectArgumentRequire)
        addNewEffectArgument(EffectArgumentPrice)
    }

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
        return BY_ID.values.toList()
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
     * Add new effect argument.
     *
     * @param argument The argument.
     */
    fun addNewEffectArgument(argument: EffectArgument) {
        EFFECT_ARGUMENTS += argument
    }

    /**
     * List of all effect arguments.
     *
     * @return The arguments.
     */
    fun effectArguments(): List<EffectArgument> {
        return EFFECT_ARGUMENTS.toList()
    }

    /**
     * Compile a group of effects.
     *
     * @param configs The effect configs.
     * @param context The context to log violations for.
     * @param chainLike If effects should allow all triggers and prevent
     *     permanent effects.
     * @return The compiled effects.
     */
    @JvmStatic
    @JvmOverloads
    fun compile(
        configs: Iterable<Config>,
        context: ViolationContext,
        chainLike: Boolean = false
    ): List<ConfiguredEffect> = configs.mapNotNull { compile(it, context, chainLike = chainLike) }.inRunOrder()

    /**
     * Compile an effect.
     *
     * @param cfg The config for the effect.
     * @param context The context to log violations for.
     * @param chainLike If effects should allow all triggers and prevent
     *     permanent effects.
     * @return The configured effect, or null if invalid.
     */
    @JvmStatic
    @JvmOverloads
    fun compile(
        cfg: Config,
        context: ViolationContext,
        chainLike: Boolean = false
    ): ConfiguredEffect? {
        val config = cfg.separatorAmbivalent()

        val uuid = UUID.randomUUID()

        val repeatData = RepeatData(1, 0.0, 0.0, 0.0)
        val injections = listOf<InjectablePlaceholder>(
            StaticPlaceholder("repeat_times") { repeatData.times.toString() },
            StaticPlaceholder("repeat_start") { repeatData.start.toString() },
            StaticPlaceholder("repeat_increment") { repeatData.increment.toString() },
            StaticPlaceholder("repeat_count") { repeatData.count.toString() }
        )
        config.addInjectablePlaceholder(injections)

        val isShorthandInlineChain = config.has("effects")

        val effect = if (isShorthandInlineChain) {
            RUN_CHAIN_INLINE // Shorthand inline chains
        } else {
            config.getString("id").let {
                val found = getByID(it)
                if (found == null) {
                    LibReforgePlugin.instance.logViolation(
                        it,
                        context,
                        ConfigViolation("id", "Invalid effect ID specified!")
                    )
                }

                found
            }
        } ?: return null

        val args = config.getSubsection("args")

        if (!isShorthandInlineChain) {
            if (effect.checkConfig(args, context)) {
                return null
            }
        }

        val filter = config.getSubsectionOrNull("filters").let {
            if (effect.isPermanent && it != null) {
                LibReforgePlugin.instance.logViolation(
                    effect.id,
                    context,
                    ConfigViolation("filters", "Specified effect does not support filters")
                )

                return@let null
            }

            it
        } ?: emptyConfig()

        val triggers = config.getStrings("triggers").let {
            val triggers = mutableListOf<Trigger>()

            if (it.isNotEmpty() && effect.isPermanent) {
                LibReforgePlugin.instance.logViolation(
                    effect.id,
                    context,
                    ConfigViolation(
                        "triggers", "Specified effect does not support triggers"
                    )
                )

                return@let null
            }

            if (chainLike) {
                if (effect.isPermanent) {
                    LibReforgePlugin.instance.logViolation(
                        effect.id,
                        context,
                        ConfigViolation(
                            "triggers", "Permanent effects are not allowed here!"
                        )
                    )

                    return@let null
                }
            }

            if (!chainLike) {
                if (!effect.isPermanent && it.isEmpty()) {
                    LibReforgePlugin.instance.logViolation(
                        effect.id,
                        context,
                        ConfigViolation(
                            "triggers", "Specified effect requires at least 1 trigger"
                        )
                    )

                    return@let null
                }
            }

            for (id in it) {
                val trigger = Triggers.getById(id)

                if (trigger == null) {
                    LibReforgePlugin.instance.logViolation(
                        effect.id,
                        context,
                        ConfigViolation(
                            "triggers", "Invalid trigger specified: $id"
                        )
                    )

                    return@let null
                }

                if (!effect.supportsTrigger(trigger)) {
                    LibReforgePlugin.instance.logViolation(
                        effect.id,
                        context,
                        ConfigViolation(
                            "triggers", "Specified effect does not support trigger $id"
                        )
                    )
                }

                triggers.add(trigger)
            }

            triggers
        } ?: return null

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("Effect-Specific conditions")
        )

        val mutators = config.getSubsections("mutators").mapNotNull {
            DataMutators.compile(it, context.with("Mutators"))
        }

        val compileData = if (isShorthandInlineChain) {
            val chain = EffectChains.compile(
                config,
                context.with("Effects"),
                anonymous = true
            )

            RUN_CHAIN_INLINE.makeCompileData(args, chain)
        } else {
            effect.makeCompileData(args, context)
        }

        return ConfiguredEffect(
            effect,
            args,
            filter,
            triggers,
            uuid,
            conditions,
            mutators,
            compileData,
            repeatData
        )
    }
}
