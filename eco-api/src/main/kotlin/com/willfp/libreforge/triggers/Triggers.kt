package com.willfp.libreforge.triggers

import com.willfp.libreforge.triggers.triggers.TriggerAltClick
import com.willfp.libreforge.triggers.triggers.TriggerBowAttack
import com.willfp.libreforge.triggers.triggers.TriggerFallDamage
import com.willfp.libreforge.triggers.triggers.TriggerGainXp
import com.willfp.libreforge.triggers.triggers.TriggerHeal
import com.willfp.libreforge.triggers.triggers.TriggerJump
import com.willfp.libreforge.triggers.triggers.TriggerKill
import com.willfp.libreforge.triggers.triggers.TriggerLoseHunger
import com.willfp.libreforge.triggers.triggers.TriggerMeleeAttack
import com.willfp.libreforge.triggers.triggers.TriggerMineBlock
import com.willfp.libreforge.triggers.triggers.TriggerProjectileHit
import com.willfp.libreforge.triggers.triggers.TriggerProjectileLaunch
import com.willfp.libreforge.triggers.triggers.TriggerShieldBlock
import com.willfp.libreforge.triggers.triggers.TriggerTakeDamage
import com.willfp.libreforge.triggers.triggers.TriggerTakeEntityDamage
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
