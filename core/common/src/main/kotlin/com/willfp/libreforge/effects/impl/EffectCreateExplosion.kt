package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.Listener

object EffectCreateExplosion : Effect<NoCompileData>("create_explosion"), Listener {
    override val description = "Creates one or more explosions at the trigger location."
    override val categories = setOf("world", "combat")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of explosions!",
            description = "The number of explosions to create (one per tick). Supports expressions.",
            type = ArgType.EXPRESSION
        )
        require(
            "power",
            "You must specify the explosion power!",
            description = "The explosion power. Vanilla TNT is 4. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "player_as_damager",
            description = "If true, the player is attributed as the source of explosion damage.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "create_fire",
            description = "Whether the explosion creates fire.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "break_blocks",
            description = "Whether the explosion breaks blocks.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false

        val damager = config.getBoolOrNull("player_as_damager") ?: false
        val source = if (damager) data.player else null

        val amount = config.getIntFromExpression("amount", data)
        val power = config.getDoubleFromExpression("power", data)
        val fire = config.getBoolOrNull("create_fire") ?: true
        val breakBlocks = config.getBoolOrNull("break_blocks") ?: true

        for (i in 1..amount) {
            plugin.scheduler.runLater(i.toLong()) {
                world.createExplosion(location, power.toFloat(), fire, breakBlocks, source)
                }
            }
        return true
    }
}