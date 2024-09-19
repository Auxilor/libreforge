package com.willfp.libreforge.triggers

import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.impl.*

object Triggers : Registry<Trigger>() {
    private val groupRegistry = Registry<TriggerGroup>()

    /**
     * Get a trigger by [id].
     *
     * This will enable the trigger.
     */
    override fun get(id: String): Trigger? {
        return doGet(id)?.apply {
            enable()
        }
    }

    private fun doGet(id: String): Trigger? {
        for (group in groupRegistry.values()) {
            if (id.startsWith("${group.prefix}_")) {
                return group.create(id.removePrefix("${group.prefix}_"))
            }
        }

        return super.get(id)
    }

    /**
     * Register a new [triggerGroup].
     */
    fun register(triggerGroup: TriggerGroup) {
        groupRegistry.register(triggerGroup)
    }

    /**
     * Get a predicate requiring certain trigger parameters.
     */
    fun withParameters(parameters: Set<TriggerParameter>): (Trigger, MutatorList) -> Boolean {
        return { trigger, mutators ->
            trigger.parameters
                .flatMap { param -> param.inheritsFrom.toSet().plusElement(param) }
                .toSet()
                .let { mutators.transform(it) }
                .containsAll(parameters)
        }
    }

    init {
        register(TriggerGroupCustom)
        register(TriggerGroupStatic)
        register(TriggerGroupGlobalStatic)
        register(TriggerAltClick)
        register(TriggerBite)
        register(TriggerBlockItemDrop)
        register(TriggerBowAttack)
        register(TriggerBreed)
        register(TriggerBrew)
        register(TriggerCastRod)
        register(TriggerCatchEntity)
        register(TriggerCatchFish)
        register(TriggerCatchFishFail)
        register(TriggerChangeArmor)
        register(TriggerChangeWorld)
        register(TriggerConsume)
        register(TriggerCraft)
        register(TriggerDamageItem)
        register(TriggerDeath)
        register(TriggerDeployElytra)
        register(TriggerDropItem)
        register(TriggerEmptyBucket)
        register(TriggerEnchantItem)
        register(TriggerEnterBed)
        register(TriggerEntityItemDrop)
        register(TriggerEntityTarget)
        register(TriggerFallDamage)
        register(TriggerFillBucket)
        register(TriggerGainHunger)
        register(TriggerGainXp)
        register(TriggerHeadshot)
        register(TriggerHeal)
        register(TriggerHoldItem)
        register(TriggerHookInGround)
        register(TriggerItemBreak)
        register(TriggerJoin)
        register(TriggerJump)
        register(TriggerKill)
        register(TriggerLeashEntity)
        register(TriggerLeave)
        register(TriggerLeaveBed)
        register(TriggerLevelUpXp)
        register(TriggerLoseHunger)
        register(TriggerLosePotionEffect)
        register(TriggerMeleeAttack)
        register(TriggerMineBlock)
        register(TriggerMineBlockProgress)
        register(TriggerMove)
        register(TriggerPickUpItem)
        register(TriggerPlaceBlock)
        register(TriggerPotionEffect)
        register(TriggerProjectileHit)
        register(TriggerProjectileLaunch)
        register(TriggerReelIn)
        register(TriggerRespawn)
        register(TriggerSellItem)
        register(TriggerSendMessage)
        register(TriggerShearEntity)
        register(TriggerShieldBlock)
        register(TriggerShootBow)
        register(TriggerSmelt)
        register(TriggerSwapHands)
        register(TriggerTakeDamage)
        register(TriggerTakeEntityDamage)
        register(TriggerToggleFlight)
        register(TriggerToggleSneak)
        register(TriggerToggleSprint)
        register(TriggerWinRaid)
        register(TriggerDrink)
        register(TriggerChangeChunk)
        register(TriggerEnable)
        register(TriggerDisable)
        register(TriggerTeleport)
        register(TriggerRunCommand)
        register(TriggerBrewIngredient)
        register(TriggerClickEntity)
        register(TriggerLevelUpItem)
        register(TriggerTameAnimal)
        register(TriggerClickBlock)
        register(TriggerEntityBreakDoor)
        register(TriggerEntityCatchFireFromBlock)
        register(TriggerEntityCatchFireFromEntity)
        register(TriggerEntityDamage)
        register(TriggerEntityDamageByEntity)
        register(TriggerEntityDeath)
        register(TriggerEntitySpawn)
        register(TriggerEntityTeleport)
        register(TriggerCompleteAdvancement)
        register(TriggerSmithItem)
        register(TriggerCollectHoneycomb)
        register(TriggerCollectHoney)
    }
}
