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
import org.bukkit.attribute.Attribute

object EffectVictimSpeedMultiplier : Effect<NoCompileData>("victim_speed_multiplier") {
    override val description = "Temporarily multiplies the victim's movement speed for a given duration."
    override val categories = setOf("movement", "combat", "attribute")

    private const val META_KEY = "libreforge-vms"

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the speed multiplier!",
            description = "The value to multiply the victim's movement speed by. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "0.5"
        )
        require(
            "duration",
            "You must specify the duration!",
            description = "How many ticks the speed change lasts. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "100"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val attribute = victim.getAttribute(Attribute.MOVEMENT_SPEED) ?: return false
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
