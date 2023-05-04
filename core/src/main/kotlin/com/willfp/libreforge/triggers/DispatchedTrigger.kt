package com.willfp.libreforge.triggers

import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholders
import org.bukkit.entity.Player

data class DispatchedTrigger(
    val player: Player,
    val trigger: Trigger,
    val data: TriggerData
) {
    private val _placeholders = mutableListOf<NamedValue>()

    val placeholders: List<InjectablePlaceholder>
        get() = _placeholders.flatMap { it.placeholders }

    init {
        for (placeholder in TriggerPlaceholders) {
            _placeholders += placeholder.createPlaceholders(this)
        }
    }

    fun addPlaceholder(placeholder: NamedValue) {
        _placeholders += placeholder
    }

    fun addPlaceholders(placeholder: Iterable<NamedValue>) {
        _placeholders += placeholder
    }

    companion object {
        fun DispatchedTrigger.inheritPlaceholders(other: DispatchedTrigger): DispatchedTrigger {
            _placeholders += other._placeholders
            return this
        }
    }
}
