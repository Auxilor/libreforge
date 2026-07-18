package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTimeBomb : Effect<NoCompileData>("time_bomb") {
    override val description = "Marks the victim to explode after a fuse delay, with an optional glow effect while the timer counts down."
    override val categories = setOf("combat", "world")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "fuse",
            "You must specify the fuse duration in ticks!",
            description = "How many ticks before the explosion occurs. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "20 * %level%"
        )
        require(
            "power",
            "You must specify the explosion power!",
            description = "The power of the explosion. Vanilla TNT is 4. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "break_blocks",
            description = "Whether the explosion breaks blocks.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "glow",
            description = "Whether the victim glows while waiting for the explosion.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val fuse = config.getIntFromExpression("fuse", data)
        val power = config.getDoubleFromExpression("power", data)
        val breakBlocks = config.getBoolOrNull("break_blocks") ?: false
        val glow = config.getBoolOrNull("glow") ?: true

        if (glow) victim.isGlowing = true

        plugin.scheduler.runLater(fuse.toLong()) {
            if (glow) victim.isGlowing = false
            if (!victim.isDead) {
                val loc = victim.location
                loc.world?.createExplosion(loc, power.toFloat(), false, breakBlocks, victim)
            }
        }

        return true
    }
}
