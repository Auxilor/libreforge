package com.willfp.libreforge.triggers

import com.willfp.libreforge.Holder
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.NamedArgument
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

data class InvocationData internal constructor(
    val player: Player,
    val data: TriggerData,
    val holder: Holder,
    val trigger: Trigger,
    val compileData: CompileData?,
    val value: Double
) {
    fun createPlaceholders(): Collection<NamedArgument> {
        val args = mutableListOf<NamedArgument>()

        args += NamedArgument(
            listOf("trigger_value", "triggervalue", "trigger", "value", "tv", "v", "t"),
            value.toString()
        )

        if (data.victim != null) {
            args += NamedArgument(
                "victim_health",
                data.victim.health
            )

            args += NamedArgument(
                "victim_max_health",
                data.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
            )
        }

        return args
    }
}
