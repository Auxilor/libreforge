package com.willfp.libreforge.integrations.paper.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object ConditionInRain : Condition<NoCompileData>("in_rain") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<Entity>() ?: return false

        return entity.isInRain
    }
}
