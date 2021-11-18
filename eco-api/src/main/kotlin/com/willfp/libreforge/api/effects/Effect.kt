package com.willfp.libreforge.api.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.ConfigurableProperty
import com.willfp.libreforge.api.Watcher
import com.willfp.libreforge.api.filter.Filter
import com.willfp.libreforge.internal.filter.CompoundFilter
import com.willfp.libreforge.internal.filter.FilterBlock
import com.willfp.libreforge.internal.filter.FilterEmpty
import com.willfp.libreforge.internal.filter.FilterLivingEntity
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.util.UUID

abstract class Effect(
    id: String
) : ConfigurableProperty(id), Watcher {
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

    /**
     * Get filter from config.
     *
     * @return The filter.
     */
    fun getFilter(config: JSONConfig): Filter {
        val filters = mutableListOf<Filter>()

        for (filterConfig in config.getSubsections("filters")) {
            filters.add(
                when (filterConfig.getString("id", false)) {
                    "block" -> FilterBlock(filterConfig)
                    "entity" -> FilterLivingEntity(filterConfig)
                    else -> FilterEmpty()
                }
            )
        }

        return CompoundFilter(*filters.toTypedArray())
    }
}