package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.util.*

abstract class Effect(
    id: String,
    val supportsFilters: Boolean = false,
    val applicableTriggers: Collection<Trigger> = emptyList()
) : ConfigurableProperty(id) {
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
     * Generate a NamespacedKey with a specified offset.
     *
     * @param offset The offset.
     * @return The NamespacedKey.
     */
    fun getNamespacedKey(
        offset: Int
    ): NamespacedKey {
        return this.plugin.namespacedKeyFactory.create("${id}_$offset")
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

    open fun handle(data: TriggerData, config: JSONConfig) {
        // Override when needed
    }
}