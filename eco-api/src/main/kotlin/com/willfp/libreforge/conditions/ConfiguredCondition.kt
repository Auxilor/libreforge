package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.BlankHolder
import com.willfp.libreforge.Holder
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

data class ConfiguredCondition internal constructor(
    val condition: Condition,
    val config: Config,
    val notMetLines: List<String>?,
    val notMetEffects: Collection<ConfiguredEffect>,
    val compileData: CompileData? = null,
    val inverse: Boolean = false
) {
    fun isMet(player: Player): Boolean {
        val raw = condition.isConditionMet(player, config, compileData)

        return raw != inverse
    }
}

fun Iterable<ConfiguredCondition>.isMet(player: Player): Boolean =
    this.all { it.isMet(player) }
