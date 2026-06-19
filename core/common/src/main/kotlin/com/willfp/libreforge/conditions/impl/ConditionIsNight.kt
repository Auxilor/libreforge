package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition

object ConditionIsNight : Condition<NoCompileData>("is_night") {
    override val description = "Passes when it is nighttime in the dispatcher's world."

    override val categories = setOf("world")

    override val additionalInfo = listOf(
        "Night is defined as world time between 12301 and 23849 ticks (inclusive)."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false
        return location.world.time in 12301..23849
    }
}
