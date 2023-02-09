package com.willfp.libreforge.triggers

import com.willfp.libreforge.triggers.impl.TriggerAltClick
import com.willfp.libreforge.triggers.impl.TriggerBite
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
import com.willfp.libreforge.triggers.impl.TriggerBowAttack
import com.willfp.libreforge.triggers.impl.TriggerBreed
import com.willfp.libreforge.triggers.impl.TriggerBrew
import com.willfp.libreforge.triggers.impl.TriggerCastRod
import com.willfp.libreforge.triggers.impl.TriggerCatchEntity
import com.willfp.libreforge.triggers.impl.TriggerCatchFish
import com.willfp.libreforge.triggers.impl.TriggerCatchFishFail
import com.willfp.libreforge.triggers.impl.TriggerChangeArmor
import com.willfp.libreforge.triggers.impl.TriggerChangeWorld
import com.willfp.libreforge.triggers.impl.TriggerConsume
import com.willfp.libreforge.triggers.impl.TriggerCraft
import com.willfp.libreforge.triggers.impl.TriggerDamageItem
import com.willfp.libreforge.triggers.impl.TriggerDeath
import com.willfp.libreforge.triggers.impl.TriggerDeployElytra
import com.willfp.libreforge.triggers.impl.TriggerDropItem
import com.willfp.libreforge.triggers.impl.TriggerEmptyBucket
import com.willfp.libreforge.triggers.impl.TriggerEnchantItem
import com.willfp.libreforge.triggers.impl.TriggerEnterBed
import com.willfp.libreforge.triggers.impl.TriggerEntityItemDrop
import com.willfp.libreforge.triggers.impl.TriggerEntityTarget
import com.willfp.libreforge.triggers.impl.TriggerFallDamage
import com.willfp.libreforge.triggers.impl.TriggerFillBucket
import com.willfp.libreforge.triggers.impl.TriggerGainHunger
import com.willfp.libreforge.triggers.impl.TriggerGainXp
import com.willfp.libreforge.triggers.impl.TriggerGroupCustom
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
import com.willfp.libreforge.triggers.impl.TriggerSellItem
import com.willfp.libreforge.triggers.impl.TriggerSendMessage
import com.willfp.libreforge.triggers.impl.TriggerShearEntity
import com.willfp.libreforge.triggers.impl.TriggerShieldBlock
import com.willfp.libreforge.triggers.impl.TriggerShootBow
import com.willfp.libreforge.triggers.impl.TriggerSmelt
import com.willfp.libreforge.triggers.impl.TriggerSwapHands
import com.willfp.libreforge.triggers.impl.TriggerTakeDamage
import com.willfp.libreforge.triggers.impl.TriggerTakeEntityDamage
import com.willfp.libreforge.triggers.impl.TriggerToggleFlight
import com.willfp.libreforge.triggers.impl.TriggerToggleSneak
import com.willfp.libreforge.triggers.impl.TriggerToggleSprint
import com.willfp.libreforge.triggers.impl.TriggerTridentAttack
import com.willfp.libreforge.triggers.impl.TriggerWinRaid

object Triggers {
    private val registry = mutableMapOf<String, Trigger>()
    private val groupRegistry = mutableMapOf<String, TriggerGroup>()

    /**
     * Get a trigger by [id].
     */
    fun getByID(id: String): Trigger? {
        for ((prefix, group) in groupRegistry) {
            if (id.startsWith("${prefix}_")) {
                return group.create(id.removePrefix("${prefix}_"))
            }
        }

        return registry[id.lowercase()]
    }

    /**
     * Register a new [trigger].
     */
    fun register(trigger: Trigger) {
        registry[trigger.id] = trigger
    }

    /**
     * Register a new [triggerGroup].
     */
    fun register(triggerGroup: TriggerGroup) {
        groupRegistry[triggerGroup.prefix] = triggerGroup
    }

    /**
     * Get a predicate requiring certain trigger parameters.
     */
    fun withParameters(parameters: Set<TriggerParameter>): (Trigger) -> Boolean {
        return {
            it.parameters.flatMap { param -> param.inheritsFrom.toList().plusElement(param) }.containsAll(parameters)
        }
    }

    init {
        register(TriggerGroupCustom)
        register(TriggerGroupStatic)

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
        register(TriggerTridentAttack)
        register(TriggerWinRaid)
    }
}
