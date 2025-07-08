package com.willfp.libreforge.triggers

import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.impl.TriggerAltClick
import com.willfp.libreforge.triggers.impl.TriggerBite
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
import com.willfp.libreforge.triggers.impl.TriggerBoneMealCrop
import com.willfp.libreforge.triggers.impl.TriggerBowAttack
import com.willfp.libreforge.triggers.impl.TriggerBreed
import com.willfp.libreforge.triggers.impl.TriggerBrew
import com.willfp.libreforge.triggers.impl.TriggerBrewIngredient
import com.willfp.libreforge.triggers.impl.TriggerCastRod
import com.willfp.libreforge.triggers.impl.TriggerCatchEntity
import com.willfp.libreforge.triggers.impl.TriggerCatchFish
import com.willfp.libreforge.triggers.impl.TriggerCatchFishFail
import com.willfp.libreforge.triggers.impl.TriggerChangeArmor
import com.willfp.libreforge.triggers.impl.TriggerChangeChunk
import com.willfp.libreforge.triggers.impl.TriggerChangeWorld
import com.willfp.libreforge.triggers.impl.TriggerClickBlock
import com.willfp.libreforge.triggers.impl.TriggerClickEntity
import com.willfp.libreforge.triggers.impl.TriggerCompleteAdvancement
import com.willfp.libreforge.triggers.impl.TriggerConsume
import com.willfp.libreforge.triggers.impl.TriggerCraft
import com.willfp.libreforge.triggers.impl.TriggerDamageItem
import com.willfp.libreforge.triggers.impl.TriggerDeath
import com.willfp.libreforge.triggers.impl.TriggerDeployElytra
import com.willfp.libreforge.triggers.impl.TriggerDisable
import com.willfp.libreforge.triggers.impl.TriggerDrink
import com.willfp.libreforge.triggers.impl.TriggerDropItem
import com.willfp.libreforge.triggers.impl.TriggerEmptyBucket
import com.willfp.libreforge.triggers.impl.TriggerEnable
import com.willfp.libreforge.triggers.impl.TriggerEnchantItem
import com.willfp.libreforge.triggers.impl.TriggerEnterBed
import com.willfp.libreforge.triggers.impl.TriggerEntityBreakDoor
import com.willfp.libreforge.triggers.impl.TriggerEntityCatchFireFromBlock
import com.willfp.libreforge.triggers.impl.TriggerEntityCatchFireFromEntity
import com.willfp.libreforge.triggers.impl.TriggerEntityDamage
import com.willfp.libreforge.triggers.impl.TriggerEntityDamageByEntity
import com.willfp.libreforge.triggers.impl.TriggerEntityDeath
import com.willfp.libreforge.triggers.impl.TriggerEntityItemDrop
import com.willfp.libreforge.triggers.impl.TriggerEntitySpawn
import com.willfp.libreforge.triggers.impl.TriggerEntityTarget
import com.willfp.libreforge.triggers.impl.TriggerEntityTeleport
import com.willfp.libreforge.triggers.impl.TriggerFallDamage
import com.willfp.libreforge.triggers.impl.TriggerFillBucket
import com.willfp.libreforge.triggers.impl.TriggerGainHunger
import com.willfp.libreforge.triggers.impl.TriggerGainXp
import com.willfp.libreforge.triggers.impl.TriggerGroupCustom
import com.willfp.libreforge.triggers.impl.TriggerGroupGlobalStatic
import com.willfp.libreforge.triggers.impl.TriggerGroupStatic
import com.willfp.libreforge.triggers.impl.TriggerHeadshot
import com.willfp.libreforge.triggers.impl.TriggerHeal
import com.willfp.libreforge.triggers.impl.TriggerHoldItem
import com.willfp.libreforge.triggers.impl.TriggerHookInGround
import com.willfp.libreforge.triggers.impl.TriggerItemBreak
import com.willfp.libreforge.triggers.impl.TriggerJoin
import com.willfp.libreforge.triggers.impl.TriggerJump
import com.willfp.libreforge.triggers.impl.TriggerKill
import com.willfp.libreforge.triggers.impl.TriggerLeashEntity
import com.willfp.libreforge.triggers.impl.TriggerLeave
import com.willfp.libreforge.triggers.impl.TriggerLeaveBed
import com.willfp.libreforge.triggers.impl.TriggerLevelUpItem
import com.willfp.libreforge.triggers.impl.TriggerLevelUpXp
import com.willfp.libreforge.triggers.impl.TriggerLoseHunger
import com.willfp.libreforge.triggers.impl.TriggerLosePotionEffect
import com.willfp.libreforge.triggers.impl.TriggerMeleeAttack
import com.willfp.libreforge.triggers.impl.TriggerMineBlock
import com.willfp.libreforge.triggers.impl.TriggerMineBlockProgress
import com.willfp.libreforge.triggers.impl.TriggerMove
import com.willfp.libreforge.triggers.impl.TriggerPickUpItem
import com.willfp.libreforge.triggers.impl.TriggerPlaceBlock
import com.willfp.libreforge.triggers.impl.TriggerPotionEffect
import com.willfp.libreforge.triggers.impl.TriggerProjectileHit
import com.willfp.libreforge.triggers.impl.TriggerProjectileLaunch
import com.willfp.libreforge.triggers.impl.TriggerReelIn
import com.willfp.libreforge.triggers.impl.TriggerRespawn
import com.willfp.libreforge.triggers.impl.TriggerRingBell
import com.willfp.libreforge.triggers.impl.TriggerRunCommand
import com.willfp.libreforge.triggers.impl.TriggerSellItem
import com.willfp.libreforge.triggers.impl.TriggerSendMessage
import com.willfp.libreforge.triggers.impl.TriggerShearEntity
import com.willfp.libreforge.triggers.impl.TriggerShieldBlock
import com.willfp.libreforge.triggers.impl.TriggerShootBow
import com.willfp.libreforge.triggers.impl.TriggerSmelt
import com.willfp.libreforge.triggers.impl.TriggerSmithItem
import com.willfp.libreforge.triggers.impl.TriggerSwapHands
import com.willfp.libreforge.triggers.impl.TriggerTakeDamage
import com.willfp.libreforge.triggers.impl.TriggerTakeEntityDamage
import com.willfp.libreforge.triggers.impl.TriggerTameAnimal
import com.willfp.libreforge.triggers.impl.TriggerTeleport
import com.willfp.libreforge.triggers.impl.TriggerToggleFlight
import com.willfp.libreforge.triggers.impl.TriggerToggleSneak
import com.willfp.libreforge.triggers.impl.TriggerToggleSprint
import com.willfp.libreforge.triggers.impl.TriggerUnleashEntity
import com.willfp.libreforge.triggers.impl.TriggerWinRaid

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
        register(TriggerBoneMealCrop)
        register(TriggerBowAttack)
        register(TriggerBreed)
        register(TriggerBrew)
        register(TriggerBrewIngredient)
        register(TriggerCastRod)
        register(TriggerCatchEntity)
        register(TriggerCatchFish)
        register(TriggerCatchFishFail)
        register(TriggerChangeArmor)
        register(TriggerChangeChunk)
        register(TriggerChangeWorld)
        register(TriggerClickBlock)
        register(TriggerClickEntity)
        register(TriggerCompleteAdvancement)
        register(TriggerConsume)
        register(TriggerCraft)
        register(TriggerDamageItem)
        register(TriggerDeath)
        register(TriggerDeployElytra)
        register(TriggerDisable)
        register(TriggerDrink)
        register(TriggerDropItem)
        register(TriggerEmptyBucket)
        register(TriggerEnable)
        register(TriggerEnchantItem)
        register(TriggerEnterBed)
        register(TriggerEntityBreakDoor)
        register(TriggerEntityCatchFireFromBlock)
        register(TriggerEntityCatchFireFromEntity)
        register(TriggerEntityDamage)
        register(TriggerEntityDamageByEntity)
        register(TriggerEntityDeath)
        register(TriggerEntityItemDrop)
        register(TriggerEntitySpawn)
        register(TriggerEntityTarget)
        register(TriggerEntityTeleport)
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
        register(TriggerLevelUpItem)
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
        register(TriggerRingBell)
        register(TriggerRunCommand)
        register(TriggerSellItem)
        register(TriggerSendMessage)
        register(TriggerShearEntity)
        register(TriggerShieldBlock)
        register(TriggerShootBow)
        register(TriggerSmelt)
        register(TriggerSmithItem)
        register(TriggerSwapHands)
        register(TriggerTakeDamage)
        register(TriggerTakeEntityDamage)
        register(TriggerTameAnimal)
        register(TriggerTeleport)
        register(TriggerToggleFlight)
        register(TriggerToggleSneak)
        register(TriggerToggleSprint)
        register(TriggerUnleashEntity)
        register(TriggerWinRaid)
    }
}
