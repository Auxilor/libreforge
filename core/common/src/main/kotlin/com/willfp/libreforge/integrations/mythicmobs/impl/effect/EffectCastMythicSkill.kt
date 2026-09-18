package com.willfp.libreforge.integrations.mythicmobs.impl.effect

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.core.utils.MythicUtil
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectCastMythicSkill : Effect<NoCompileData>("cast_mythic_skill") {
    override val description = "Casts a MythicMobs skill from the player, targeting the victim or the player's current target."
    override val categories = setOf("meta")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "skill",
            "You must specify the skill to cast!",
            description = "The MythicMobs skill name to cast.",
            type = ArgType.STRING
        )
        optional(
            "victim_to_player",
            description = "Whether to target the casting player instead of the victim. Defaults to false.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "power",
            description = "The skill power, a multiplier on the skill's damage mechanics (e.g. 1.5 deals 50% more damage). Also scales some other mechanics such as projectiles and leap, and is available in the skill as <skill.power>. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "1",
            example = "1.5"
        )
        optional(
            "variables",
            description = "A subsection of key-value pairs to set as skill variables, available in the skill as <skill.var.key>. Numeric values support expressions, anything else is passed as text.",
            type = ArgType.MAP,
            mapKeyType = ArgType.STRING,
            mapValueType = ArgType.STRING
        )
        optional(
            "victim_as_trigger",
            description = "Whether to use the victim as the skill's trigger entity, for @Trigger targeters. Defaults to false, which uses the casting player.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "target_location",
            description = "Whether to pass the trigger location to the skill as a location target, for @TargetLocation targeters. Defaults to false.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player: Player = data.player ?: return false

        var victim: LivingEntity? = data.victim ?: MythicUtil.getTargetedEntity(player)

        if (config.getBoolOrNull("victim_to_player") == true) {
            victim = player
        }

        val skill = config.getString("skill")

        val targets = listOfNotNull(victim)

        MythicBukkit.inst().apiHelper.castSkill(
            player,
            skill,
            if (config.getBoolOrNull("victim_as_trigger") == true) victim ?: player else player,
            player.location,
            targets,
            if (config.getBoolOrNull("target_location") == true) listOfNotNull(data.location) else null,
            if (config.has("power")) config.getDoubleFromExpression("power", data).toFloat() else 1.0F
        ) { metadata ->
            val variables = config.getSubsection("variables")
            val context = config.toPlaceholderContext(data)

            for (key in variables.getKeys(false)) {
                val raw = variables.getString(key)
                val value = evaluateExpressionOrNull(raw, context)?.takeIf { it.isFinite() }

                if (value != null) {
                    metadata.variables.putDouble(key, value)
                } else {
                    metadata.variables.putString(key, raw.formatEco(context))
                }
            }
        }

        return true
    }
}
