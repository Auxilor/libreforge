package com.willfp.libreforge.effects.impl.animations

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import org.bukkit.Location
import java.util.Objects

abstract class Animation<T, T2>(
    override val id: String
) : Compilable<T>() {
    /**
     * Set up the animation.
     */
    @Suppress("UNCHECKED_CAST")
    open fun setUp(
        sourceLocation: Location,
        direction: Float3,
        config: Config,
        data: TriggerData,
        compileData: T
    ): T2 = Unit as T2

    /**
     * Play the animation, returning true if the animation is complete.
     */
    abstract fun play(
        tick: Int,
        sourceLocation: Location,
        direction: Float3,
        config: Config,
        triggerData: TriggerData,
        compileData: T,
        data: T2
    ): Boolean

    /**
     * Finish the animation.
     */
    open fun finish(
        sourceLocation: Location,
        direction: Float3,
        config: Config,
        triggerData: TriggerData,
        compileData: T,
        data: T2
    ) {

    }

    override fun equals(other: Any?): Boolean {
        if (other !is Animation<*, *>) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}
