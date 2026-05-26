package com.willfp.libreforge.biomes

import com.willfp.eco.core.integrations.IntegrationRegistry
import org.bukkit.Location

val biomeProviders = IntegrationRegistry<BiomeProvider>()

@Deprecated("Renamed to biomeProviders", ReplaceWith("biomeProviders"))
val customBiomesIntegrations get() = biomeProviders

val Location.namedBiome: NamedBiome?
    get() {
        val world = this.world ?: return null
        val vanilla = world.getBiome(this)

        return if (vanilla.key.key.lowercase() == "custom") {
            biomeProviders
                .firstOrNull { it.getBiome(this) != null }
                ?.getBiome(this)
        } else {
            VanillaNamedBiome(vanilla)
        }
    }
