package com.willfp.libreforge.triggers

import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.libreforge.GroupedStaticPlaceholder
import org.bukkit.entity.Player

data class DispatchedTrigger(
    val player: Player,
    val trigger: Trigger,
    val data: TriggerData
) {
    private val _placeholders = mutableListOf<GroupedStaticPlaceholder>()

    val placeholders: List<StaticPlaceholder>
        get() = _placeholders.flatMap { it.placeholders }

    fun addPlaceholder(placeholder: GroupedStaticPlaceholder) {
        _placeholders += placeholder
    }

    companion object {
        fun DispatchedTrigger.inheritPlaceholders(other: DispatchedTrigger): DispatchedTrigger {
            _placeholders += other._placeholders
            return this
        }
    }
}
