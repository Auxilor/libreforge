package com.willfp.libreforge.triggers

import com.willfp.libreforge.Holder
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.events.TriggerCreatePlaceholdersEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

data class InvocationData internal constructor(
    val player: Player,
    val data: TriggerData,
    val holder: Holder,
    val trigger: Trigger,
    val compileData: CompileData?,
    val value: Double
) {
    internal fun createPlaceholders(): Collection<NamedArgument> {
        val event = TriggerCreatePlaceholdersEvent(player, holder, trigger, data, value)
        Bukkit.getPluginManager().callEvent(event)

        return event.placeholders
    }
}
