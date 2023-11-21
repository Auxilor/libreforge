package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object ConditionHasPotionEffect : Condition<NoCompileData>("has_potion_effect") {
    override val arguments = arguments {
        require("effect", "You must specify the potion effect!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val livingEntity = dispatcher.get<LivingEntity>() ?: return false

        return livingEntity.activePotionEffects.map { it.type.name }.containsIgnoreCase(
            config.getString("effect")
        )
    }
}
