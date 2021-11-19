package com.willfp.libreforge.api.triggers

import com.willfp.libreforge.internal.triggers.TriggerBowAttack
import com.willfp.libreforge.internal.triggers.TriggerMeleeAttack
import com.willfp.libreforge.internal.triggers.TriggerMineBlock
import com.willfp.libreforge.internal.triggers.TriggerJump
import com.willfp.libreforge.internal.triggers.TriggerKill
import com.willfp.libreforge.internal.triggers.TriggerTridentAttack

object Triggers {
    private val BY_ID = mutableMapOf<String, Trigger>()

    val MELEE_ATTACK: Trigger = TriggerMeleeAttack()
    val BOW_ATTACK: Trigger = TriggerBowAttack()
    val TRIDENT_ATTACK: Trigger = TriggerTridentAttack()
    val MINE_BLOCK: Trigger = TriggerMineBlock()
    val JUMP: Trigger = TriggerJump()
    val KILL: Trigger = TriggerKill()

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
