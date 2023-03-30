package com.willfp.libreforge.effects.impl.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.impl.aoe.impl.AOEShapeCircle
import com.willfp.libreforge.effects.impl.aoe.impl.AOEShapeCone
import com.willfp.libreforge.effects.impl.aoe.impl.AOEShapeOffsetCircle
import com.willfp.libreforge.effects.impl.aoe.impl.AOEShapeScanInFront

@Suppress("UNUSED")
object AOEShapes : Registry<AOEShape<*>>() {
    /**
     * Compile a [config] into a AOEBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): AOEBlock<*>? {
        val shape = get(config.getString("shape"))

        if (shape == null) {
            context.log(ConfigViolation("shape", "Invalid shape ID specified!"))
            return null
        }

        return makeBlock(shape, config, context.with("shape args"))
    }

    private fun <T> makeBlock(
        shape: AOEShape<T>,
        config: Config,
        context: ViolationContext
    ): AOEBlock<T>? {
        if (!shape.checkConfig(config, context)) {
            return null
        }

        val compileData = shape.makeCompileData(config, context)

        return AOEBlock(
            shape,
            config,
            compileData,
        )
    }

    init {
        register(AOEShapeCircle)
        register(AOEShapeCone)
        register(AOEShapeOffsetCircle)
        register(AOEShapeScanInFront)
    }
}
