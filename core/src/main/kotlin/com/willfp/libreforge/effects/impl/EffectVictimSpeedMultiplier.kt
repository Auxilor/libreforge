package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute

object EffectVictimSpeedMultiplier : Effect<NoCompileData>("victim_speed_multiplier") {
    private const val META_KEY = "libreforge-vms"

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("multiplier", "You must specify the speed multiplier!")
        require("duration", "You must specify the duration!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val attribute = victim.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return false
        val attributeValue = attribute.value
        val duration = config.getIntFromExpression("duration", data)

        if (duration < 1) {
            return false
        }

        if (victim.hasMetadata(META_KEY)) {
            return false
        }

        attribute.baseValue = attributeValue * config.getDoubleFromExpression("multiplier", data)

        victim.setMetadata(META_KEY, plugin.createMetadataValue(true))

        plugin.scheduler.runLater(duration.toLong()) {
            attribute.baseValue = attributeValue
            victim.removeMetadata(META_KEY, plugin)
        }

        return true
    }
}
