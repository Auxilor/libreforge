package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.hasCondition
import com.willfp.libreforge.updateEffects
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.weather.WeatherChangeEvent

object ConditionIsStorm: Condition<NoCompileData>("is_storm") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false
        return location.world.hasStorm()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: WeatherChangeEvent) {
        for (entity in event.world.entities) {
            val dispatcher = entity.toDispatcher()

            if (!dispatcher.hasCondition(this)) return

            dispatcher.updateEffects()
        }
    }
}
