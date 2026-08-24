package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectBreakBlock : MineBlockEffect<NoCompileData>("break_block") {
    override val description = "Breaks the triggering block as if the player mined it."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        optional(
            "prevent_trigger",
            description = "Whether the broken block should fire further libreforge triggers. " +
                    "true breaks it silently, firing no events at all; keep_events breaks it normally, " +
                    "so drops and block events still happen, but stops the mine_block trigger from running again.",
            type = ArgType.STRING,
            default = "false",
            choices = listOf("false", "true", "keep_events")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false

        val player = data.player ?: return false

        val preventTriggers = preventTriggerMode(config)

        data.breakBlocksSafely(listOf(block), preventTriggers)

        return true
    }
}
