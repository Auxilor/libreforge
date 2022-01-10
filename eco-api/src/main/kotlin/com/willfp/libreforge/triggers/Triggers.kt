package com.willfp.libreforge.triggers

import com.willfp.libreforge.triggers.triggers.TriggerAltClick
import com.willfp.libreforge.triggers.triggers.TriggerBlockItemDrop
import com.willfp.libreforge.triggers.triggers.TriggerBowAttack
import com.willfp.libreforge.triggers.triggers.TriggerDeath
import com.willfp.libreforge.triggers.triggers.TriggerEntityItemDrop
import com.willfp.libreforge.triggers.triggers.TriggerFallDamage
import com.willfp.libreforge.triggers.triggers.TriggerGainHunger
import com.willfp.libreforge.triggers.triggers.TriggerGainXp
import com.willfp.libreforge.triggers.triggers.TriggerHeal
import com.willfp.libreforge.triggers.triggers.TriggerJump
import com.willfp.libreforge.triggers.triggers.TriggerKill
import com.willfp.libreforge.triggers.triggers.TriggerLoseHunger
import com.willfp.libreforge.triggers.triggers.TriggerLosePotionEffect
import com.willfp.libreforge.triggers.triggers.TriggerMeleeAttack
import com.willfp.libreforge.triggers.triggers.TriggerMineBlock
import com.willfp.libreforge.triggers.triggers.TriggerMove
import com.willfp.libreforge.triggers.triggers.TriggerPotionEffect
import com.willfp.libreforge.triggers.triggers.TriggerProjectileHit
import com.willfp.libreforge.triggers.triggers.TriggerProjectileLaunch
import com.willfp.libreforge.triggers.triggers.TriggerShieldBlock
import com.willfp.libreforge.triggers.triggers.TriggerSwapHands
import com.willfp.libreforge.integrations.paper.TriggerSwing
import com.willfp.libreforge.triggers.triggers.TriggerTakeDamage
import com.willfp.libreforge.triggers.triggers.TriggerTakeEntityDamage
import com.willfp.libreforge.triggers.triggers.TriggerToggleFlight
import com.willfp.libreforge.triggers.triggers.TriggerToggleSneak
import com.willfp.libreforge.triggers.triggers.TriggerToggleSprint
import com.willfp.libreforge.triggers.triggers.TriggerTridentAttack

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
