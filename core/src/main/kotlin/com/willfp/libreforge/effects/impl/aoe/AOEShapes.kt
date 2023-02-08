package com.willfp.libreforge.effects.impl.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext

@Suppress("UNUSED")
object AOEShapes {
    private val registry = mutableMapOf<String, AOEShape<*>>()

    /**
     * Get a shape by [id].
     */
    fun getByID(id: String): AOEShape<*>? {
        return registry[id]
    }

    /**
     * Register a new [shape].
     */
    fun register(shape: AOEShape<*>) {
        registry[shape.id] = shape
    }

    /**
     * Compile a [config] into a AOEBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): AOEBlock<*>? {
        val shape = getByID(config.getString("id"))

        if (shape == null) {
            context.log(ConfigViolation("id", "Invalid shape ID specified!"))
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
}
