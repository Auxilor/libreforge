package com.willfp.libreforge.integrations.custombiomes

import com.willfp.eco.core.integrations.IntegrationRegistry
import com.willfp.libreforge.plugin
import org.bukkit.Location
import org.bukkit.event.Listener

object CustomBiomesManager {
    /**
     * A set of all registered biomes.
     */
    private val REGISTRY = IntegrationRegistry<CustomBiomesIntegration>()

    /**
     * Register a new biomes integration.
     *
     * @param biomesIntegration The biomes integration to register.
     */
    fun register(biomesIntegration: CustomBiomesIntegration) {
        if (biomesIntegration is Listener) {
            plugin.eventManager.registerListener((biomesIntegration as Listener))
        }
        REGISTRY.register(biomesIntegration)
    }

    fun getBiomeAt(location: Location): CustomBiome? {
        val world = location.world ?: return null
        val vanilla = world.getBiome(location)
        return if (vanilla.name.equals("custom", ignoreCase = true)) {
            for (integration in REGISTRY) {
                val biome = integration.getBiome(location)
                if (biome != null) {
                    return biome
                }
            }
            null
        } else {
            CustomBiome(vanilla.name)
        }
    }
}