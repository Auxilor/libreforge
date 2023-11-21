package com.willfp.libreforge.integrations.paper.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.entity.Entity

object ConditionInLava : Condition<NoCompileData>("in_lava") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<Entity>() ?: return false
        return entity.isInLava
    }
}
