package com.willfp.libreforge.biomes

import com.willfp.libreforge.integrations.LoadableIntegration
import org.bukkit.Location

interface BiomeProvider : LoadableIntegration {
    /**
     * Get a biome at given location. (Supports vanilla biomes as well)
     *
     * @param location The location to get the biome at.
     * @return         The found biome, null otherwise
     */
    fun getBiome(location: Location?): NamedBiome?
}
