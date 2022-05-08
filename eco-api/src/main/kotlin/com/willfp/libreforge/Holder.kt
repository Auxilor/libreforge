package com.willfp.libreforge

import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import org.bukkit.entity.Player

interface Holder {
    val id: String
    val effects: Set<ConfiguredEffect>
    val conditions: Set<ConfiguredCondition>

    fun getNotMetLines(player: Player): List<String> {
        val lines = mutableListOf<String>()

        for (condition in this.conditions) {
            if (condition.isMet(player)) {
                lines.addAll(condition.notMetLines ?: continue)
            }
        }

        return lines
    }
}
