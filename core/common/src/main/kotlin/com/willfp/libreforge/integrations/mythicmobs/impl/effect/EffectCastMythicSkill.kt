package com.willfp.libreforge.integrations.mythicmobs.impl.effect

import com.willfp.eco.core.config.interfaces.Config
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
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("skill", "You must specify the skill to cast!")
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