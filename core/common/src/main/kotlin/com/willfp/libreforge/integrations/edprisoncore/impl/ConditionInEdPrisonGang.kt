package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.modules.gangs.GangUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionInEdPrisonGang : Condition<NoCompileData>("in_edprison_gang") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()?.uniqueId ?: return false

        return GangUtils.hasGang(player)
    }
}