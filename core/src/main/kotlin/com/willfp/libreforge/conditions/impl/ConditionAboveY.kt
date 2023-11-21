package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import com.willfp.libreforge.triggers.ifType
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object ConditionAboveY : Condition<NoCompileData>("above_y") {
    override val arguments = arguments {
        require("y", "You must specify the y level!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false

        return location.y >= config.getDoubleFromExpression("y", dispatcher.get())
    }
}
