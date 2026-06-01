package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object EffectRemoveVictim : Effect<NoCompileData>("remove_victim") {
    override val description = "Removes (despawns) the victim entity. Has no effect if the victim is a player."
    override val categories = setOf("combat", "entity")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        if (victim is Player) {
            return false
        }

        victim.remove()

        return true
    }
}
