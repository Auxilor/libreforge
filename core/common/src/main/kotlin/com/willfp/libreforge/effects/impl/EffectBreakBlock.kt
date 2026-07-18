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
            description = "If true, breaking this block will not fire further libreforge triggers.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false

        val player = data.player ?: return false

        val preventTriggers = config.getBool("prevent_trigger")

        player.breakBlocksSafely(listOf(block), preventTriggers)

        return true
    }
}
