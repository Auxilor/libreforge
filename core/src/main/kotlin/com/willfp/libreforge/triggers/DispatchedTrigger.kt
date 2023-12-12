package com.willfp.libreforge.triggers

import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholders
import org.bukkit.entity.Player

class DispatchedTrigger(
    val dispatcher: Dispatcher<*>,
    val trigger: Trigger,
    val data: TriggerData
) {
    private val _placeholders = mutableListOf<NamedValue>()

    val placeholders: List<InjectablePlaceholder>
        get() = _placeholders.flatMap { it.placeholders }

    @Deprecated(
        "Use dispatcher instead",
        ReplaceWith("toDispatcher().get()"),
        DeprecationLevel.ERROR
    )
    val player: Player?
        get() = dispatcher.get()

    @Deprecated(
        "DispatchedTrigger should be constructed with a Dispatcher",
        ReplaceWith("DispatchedTrigger(player.toDispatcher(), trigger, data)"),
        DeprecationLevel.ERROR
    )
    constructor(
        player: Player,
        trigger: Trigger,
        data: TriggerData,
    ) : this(player.toDispatcher(), trigger, data)

    fun addPlaceholder(placeholder: NamedValue) {
        _placeholders += placeholder
    }

    fun addPlaceholders(placeholder: Iterable<NamedValue>) {
        _placeholders += placeholder
    }

    fun inheritPlaceholders(other: DispatchedTrigger): DispatchedTrigger {
        _placeholders += other._placeholders
        return this
    }

    /**
     * Generate placeholders for some [data].
     *
     * This is called automatically when the trigger is dispatched,
     * and again after mutation with the updated [data].
     */
    internal fun generatePlaceholders(data: TriggerData = this.data) {
        for (placeholder in TriggerPlaceholders) {
            _placeholders += placeholder.createPlaceholders(data)
        }
    }

    /**
     * Copy this [DispatchedTrigger], optionally overriding some values.
     */
    fun copy(
        dispatcher: Dispatcher<*> = this.dispatcher,
        trigger: Trigger = this.trigger,
        data: TriggerData = this.data
    ): DispatchedTrigger = DispatchedTrigger(dispatcher, trigger, data).inheritPlaceholders(this)
}
