package com.willfp.libreforge.api.triggers

import com.willfp.libreforge.internal.triggers.TriggerBowAttack
import com.willfp.libreforge.internal.triggers.TriggerMeleeAttack
import com.willfp.libreforge.internal.triggers.TriggerMineBlock
import com.willfp.libreforge.internal.triggers.TriggerOnJump
import com.willfp.libreforge.internal.triggers.TriggerTridentAttack
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object Triggers {
    private val BY_ID = mutableMapOf<String, Trigger<*>>()

    val MELEE_ATTACK: Trigger<(Player, LivingEntity) -> Unit> = TriggerMeleeAttack()
    val BOW_ATTACK = TriggerBowAttack()
    val TRIDENT_ATTACK = TriggerTridentAttack()
    val MINE_BLOCK = TriggerMineBlock()
    val ON_JUMP = TriggerOnJump()

    fun values(): Set<Trigger<*>> {
        return BY_ID.values.toSet()
    }

    fun getById(id: String): Trigger<*>? {
        return BY_ID[id.lowercase()]
    }

    /**
     * Add new trigger.
     *
     * @param trigger The trigger to add.
     */
    fun addNewTrigger(trigger: Trigger<*>) {
        BY_ID.remove(trigger.id)
        BY_ID[trigger.id] = trigger
    }
}
