package com.willfp.libreforge.api.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.LibReforge
import com.willfp.libreforge.api.Watcher
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID

abstract class Effect(
    val id: String
) : Watcher {
    protected val plugin = LibReforge.instance.plugin

    init {
        postInit()
    }

    private fun postInit() {
        Effects.addNewEffect(this)
    }

    /**
     * Generate a UUID with a specified offset.
     *
     * @param offset The offset.
     * @return The UUID.
     */
    fun getUUID(
        offset: Int
    ): UUID {
        return UUID.nameUUIDFromBytes("$id$offset".toByteArray())
    }

    /**
     * Handle application of this effect.
     *
     * @param player The player.
     * @param config The config.
     */
    fun enableForPlayer(
        player: Player,
        config: JSONConfig
    ) {
        player.pushEffect(this)
        handleEnable(player, config)
    }

    protected open fun handleEnable(
        player: Player,
        config: JSONConfig
    ) {
        // Override when needed.
    }

    /**
     * Handle removal of this effect.
     *
     * @param player The player.
     */
    fun disableForPlayer(player: Player) {
        handleDisable(player)
        player.popEffect(this)
    }

    protected open fun handleDisable(player: Player) {
        // Override when needed.
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Effect) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}