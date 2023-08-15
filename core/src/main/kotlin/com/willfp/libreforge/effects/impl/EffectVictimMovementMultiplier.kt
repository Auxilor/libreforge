package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.scheduler.BukkitTask

object EffectVictimMovementMultiplier: Effect<NoCompileData>("victim_movement_multiplier") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val attribute = victim.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return false
        val attributeValue = attribute.value
        val duration = if (config.has("duration")) config.getIntFromExpression("duration") else null

        if (victim.hasMetadata("libreforge-vms")) {
            return false
        }
        attribute.baseValue = attributeValue * config.getDoubleFromExpression("multiplier")
        victim.setMetadata("libreforge-vms", plugin.createMetadataValue("1"))
        if (duration != null && duration > 0) {
            plugin.scheduler.runLater(duration.toLong()) {
                attribute.baseValue = attributeValue
                victim.removeMetadata("libreforge-vms", plugin)
            }
        }
        return true
    }

}