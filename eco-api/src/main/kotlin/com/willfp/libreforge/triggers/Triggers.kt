package com.willfp.libreforge.triggers

import com.willfp.libreforge.integrations.paper.TriggerSwing
import com.willfp.libreforge.triggers.triggers.*

@Suppress("UNUSED")
object Triggers {
    private val BY_ID = mutableMapOf<String, Trigger>()

    val MELEE_ATTACK: Trigger = TriggerMeleeAttack()
    val BOW_ATTACK: Trigger = TriggerBowAttack()
    val TRIDENT_ATTACK: Trigger = TriggerTridentAttack()
    val MINE_BLOCK: Trigger = TriggerMineBlock()
    val JUMP: Trigger = TriggerJump()
    val KILL: Trigger = TriggerKill()
    val PROJECTILE_LAUNCH: Trigger = TriggerProjectileLaunch()
    val TAKE_DAMAGE: Trigger = TriggerTakeDamage()
    val PROJECTILE_HIT: Trigger = TriggerProjectileHit()
    val FALL_DAMAGE: Trigger = TriggerFallDamage()
    val TAKE_ENTITY_DAMAGE: Trigger = TriggerTakeEntityDamage()
    val ALT_CLICK: Trigger = TriggerAltClick()
    val HEAL: Trigger = TriggerHeal()
    val LOSE_HUNGER: Trigger = TriggerLoseHunger()
    val GAIN_XP: Trigger = TriggerGainXp()
    val SHIELD_BLOCK: Trigger = TriggerShieldBlock()
    val BLOCK_ITEM_DROP: Trigger = TriggerBlockItemDrop()
    val ENTITY_ITEM_DROP: Trigger = TriggerEntityItemDrop()
    val GAIN_HUNGER: Trigger = TriggerGainHunger()
    val MOVE: Trigger = TriggerMove()
    val SWAP_HANDS: Trigger = TriggerSwapHands()
    val SWING: Trigger = TriggerSwing()
    val TOGGLE_FLIGHT: Trigger = TriggerToggleFlight()
    val TOGGLE_SNEAK: Trigger = TriggerToggleSneak()
    val TOGGLE_SPRINT: Trigger = TriggerToggleSprint()
    val POTION_EFFECT: Trigger = TriggerPotionEffect()
    val LOSE_POTION_EFFECT: Trigger = TriggerLosePotionEffect()
    val DEATH: Trigger = TriggerDeath()
    val MINE_BLOCK_PROGRESS: Trigger = TriggerMineBlockProgress()
    val ITEM_BREAK: Trigger = TriggerItemBreak()
    val BITE: Trigger = TriggerBite()
    val CAST_ROD: Trigger = TriggerCastRod()
    val CATCH_ENTITY: Trigger = TriggerCatchEntity()
    val CATCH_FISH: Trigger = TriggerCatchFish()
    val CATCH_FISH_FAIL: Trigger = TriggerCatchFishFail()
    val HOOK_IN_GROUND: Trigger = TriggerHookInGround()
    val REEL_IN: Trigger = TriggerReelIn()
    val DEPLOY_ELYTRA: Trigger = TriggerDeployElytra()
    val SHOOT_BOW: Trigger = TriggerShootBow()
    val CONSUME: Trigger = TriggerConsume()

    fun values(): Set<Trigger> {
        return BY_ID.values.toSet()
    }

    fun withParameters(vararg parameters: TriggerParameter): Set<Trigger> {
        val found = mutableSetOf<Trigger>()

        for (trigger in values()) {
            if (trigger.parameters.containsAll(parameters.toSet())) {
                found.add(trigger)
            }
        }

        return found
    }

    fun getById(id: String): Trigger? {
        if (id.startsWith("static_")) {
            val interval = id.removePrefix("static_").toIntOrNull() ?: return null
            return TriggerStatic.getWithInterval(interval)
        }

        return BY_ID[id.lowercase()]
    }

    /**
     * Add new trigger.
     *
     * @param trigger The trigger to add.
     */
    fun addNewTrigger(trigger: Trigger) {
        BY_ID.remove(trigger.id)
        BY_ID[trigger.id] = trigger
    }
}
