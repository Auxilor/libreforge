package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.nestedMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.globalPoints
import com.willfp.libreforge.points
import org.bukkit.entity.Player
import java.util.UUID

object EffectAddGlobalPoints : Effect<NoCompileData>("add_global_points") {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the amount of points!")
    }

    private val tracker = mutableMapOf<UUID, AddedPoint>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val point = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", dispatcher.get())

        tracker[identifiers.uuid] = AddedPoint(
            point, amount
        )

        globalPoints[point] += amount
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val addedPoint = tracker[identifiers.uuid] ?: return

        globalPoints[addedPoint.point] -= addedPoint.amount
    }

    private data class AddedPoint(
        val point: String, val amount: Double
    )
}
