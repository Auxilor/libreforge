package com.willfp.libreforge.triggers

import com.willfp.libreforge.triggers.triggers.TriggerBowAttack
import com.willfp.libreforge.triggers.triggers.TriggerJump
import com.willfp.libreforge.triggers.triggers.TriggerKill
import com.willfp.libreforge.triggers.triggers.TriggerMeleeAttack
import com.willfp.libreforge.triggers.triggers.TriggerMineBlock
import com.willfp.libreforge.triggers.triggers.TriggerProjectileLaunch
import com.willfp.libreforge.triggers.triggers.TriggerTridentAttack

object Triggers {
    private val BY_ID = mutableMapOf<String, Trigger>()

    val MELEE_ATTACK: Trigger = TriggerMeleeAttack()
    val BOW_ATTACK: Trigger = TriggerBowAttack()
    val TRIDENT_ATTACK: Trigger = TriggerTridentAttack()
    val MINE_BLOCK: Trigger = TriggerMineBlock()
    val JUMP: Trigger = TriggerJump()
    val KILL: Trigger = TriggerKill()
    val PROJECTILE_LAUNCH: Trigger = TriggerProjectileLaunch()

    fun values(): Set<Trigger> {
        return BY_ID.values.toSet()
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
