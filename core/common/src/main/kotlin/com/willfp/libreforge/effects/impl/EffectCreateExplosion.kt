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
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*

object EffectCreateExplosion : Effect<NoCompileData>("create_explosion"), Listener {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of explosions!")
        require("power", "You must specify the explosion power!")
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