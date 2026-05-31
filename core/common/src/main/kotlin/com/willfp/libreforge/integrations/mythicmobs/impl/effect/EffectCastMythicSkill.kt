package com.willfp.libreforge.integrations.mythicmobs.impl.effect

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
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
        require("skill", "You must specify the skill to cast!", description = "The MythicMobs skill name to cast.", type = ArgType.STRING)
        optional("victim_to_player", description = "Whether to target the casting player instead of the victim. Defaults to false.", type = ArgType.BOOLEAN, default = "false")
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
            player,
            player.location,
            targets,
            null,
            1.0F
        )

        return true
    }
}