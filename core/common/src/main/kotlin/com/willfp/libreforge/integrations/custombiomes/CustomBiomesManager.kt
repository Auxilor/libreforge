package com.willfp.libreforge.integrations.custombiomes

import com.willfp.eco.core.integrations.IntegrationRegistry
import org.bukkit.Location

val customBiomesIntegrations = IntegrationRegistry<CustomBiomesIntegration>()

val Location.namedBiome: NamedBiome?
    get() {
        val world = this.world ?: return null
        val vanilla = world.getBiome(this)

        return if (vanilla.key.key.lowercase() == "custom") {
            customBiomesIntegrations
                .firstOrNull { it.getBiome(this) != null }
                ?.getBiome(this)
        } else {
            VanillaNamedBiome(vanilla)
        }
    }
