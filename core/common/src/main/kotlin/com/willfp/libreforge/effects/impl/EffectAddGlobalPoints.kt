package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.globalPoints
import java.util.UUID

object EffectAddGlobalPoints : Effect<NoCompileData>("add_global_points") {
    override val description = "Permanently increases a global point counter while the holder is active."
    override val categories = setOf("economy", "points")

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The global point type to add to.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the amount of points!",
            description = "The amount of global points to add. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 10"
        )
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
