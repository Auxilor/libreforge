package com.willfp.libreforge.integrations.custombiomes

import com.willfp.eco.core.integrations.IntegrationRegistry
import com.willfp.libreforge.plugin
import org.bukkit.Location
import org.bukkit.event.Listener

val customBiomesIntegrations = IntegrationRegistry<CustomBiomesIntegration>()

val Location.namedBiome: NamedBiome?
    get() {
        val world = this.world ?: return null
        val vanilla = world.getBiome(this)

        return if (vanilla.name.lowercase() == "custom") {
            customBiomesIntegrations
                .firstOrNull { it.getBiome(this) != null }
                ?.getBiome(this)
        } else {
            VanillaNamedBiome(vanilla)
        }
    }
