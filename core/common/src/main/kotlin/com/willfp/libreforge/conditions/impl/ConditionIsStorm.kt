package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.Regions
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.weather.WeatherChangeEvent

object ConditionIsStorm: Condition<NoCompileData>("is_storm") {
    override val description = "Passes when the world is experiencing a storm."
    override val categories = setOf("world")
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
        val entities = if (Regions.isFolia) {
            Bukkit.getOnlinePlayers().filter { it.world == event.world }
        } else {
            event.world.entities
        }

        for (entity in entities) {
            entity.toDispatcher().updateEffects()
        }
    }
}
