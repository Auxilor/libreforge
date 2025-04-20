package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.datatypes.skills.PrimarySkillType
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent
import com.gmail.nossr50.util.player.UserManager
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionHasMcMMOLevel : Condition<NoCompileData>("has_mcmmo_skill_level") {
    override val arguments = arguments {
        require("skill", "You must specify the skill!")
        require("level", "You must specify the skill level!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val mcmmoPlayer = UserManager.getPlayer(player) ?: return false

        val skillName = config.getString("skill").uppercase() ?: return false
        val skill = try {
            PrimarySkillType.valueOf(skillName)
        } catch (e: IllegalArgumentException) {
            return false
        }

        return mcmmoPlayer.getSkillLevel(skill) >= config.getInt("level")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: McMMOPlayerLevelChangeEvent) {
        event.player.toDispatcher().updateEffects()
    }
}