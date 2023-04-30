package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

object EffectTraceback : Effect<NoCompileData>("traceback") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("seconds", "You must specify the amount of seconds to go back in time (1-30)!")
    }

    private val key = NamespacedKey(plugin, "libreforge_traceback")

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val time = config.getDoubleFromExpression("seconds", data).toInt().coerceIn(1..30)

        val times = player.pdc.get(key, Times)?: emptyList()

        // Most recent is last
        val index = times.size - time

        val location = times.getOrElse(index) { times.lastOrNull() } ?: return false

        player.teleport(location)

        return true
    }

    override fun postRegister() {
        plugin.scheduler.runTimer(20, 20) {
            for (player in Bukkit.getOnlinePlayers()) {
                val times = player.pdc.get(key, Times) ?: emptyList()
                val newTimes = (if (times.size < 29) times else times.drop(1)) + player.location

                player.pdc.set(key, Times, newTimes)
            }
        }
    }

    private object Times : PersistentDataType<Array<PersistentDataContainer>, List<Location>> {
        override fun getPrimitiveType() = Array<PersistentDataContainer> ::class.java

        @Suppress("UNCHECKED_CAST")
        override fun getComplexType() = List::class.java as Class<List<Location>>
        private val x = NamespacedKey(plugin, "x")
        private val y = NamespacedKey(plugin, "y")
        private val z = NamespacedKey(plugin, "z")
        private val yaw = NamespacedKey(plugin, "yaw")
        private val pitch = NamespacedKey(plugin, "pitch")
        private val world = NamespacedKey(plugin, "world")
        override fun fromPrimitive(
            primitive: Array<PersistentDataContainer>,
            context: PersistentDataAdapterContext
        ): List<Location> = Stream.of(*primitive)
            .map {
                val world = it.getString(world)
                Location(
                    if (world == null) null else Bukkit.getWorld(world),
                    it.getDouble(x)!!,
                    it.getDouble(y)!!,
                    it.getDouble(z)!!,
                    it.getFloat(yaw)!!,
                    it.getFloat(pitch)!!
                )
            }.toList()

        override fun toPrimitive(
            complex: List<Location>,
            context: PersistentDataAdapterContext
        ): Array<PersistentDataContainer> = complex.stream()
            .map {
                val pdc = context.newPersistentDataContainer()
                if (it.world != null) pdc.setString(world, it.world.name)
                pdc.setDouble(x, it.x)
                pdc.setDouble(y, it.y)
                pdc.setDouble(z, it.z)
                pdc.setFloat(yaw, it.yaw)
                pdc.setFloat(pitch, it.pitch)
                pdc
            }.toList().toTypedArray()

    }
}
