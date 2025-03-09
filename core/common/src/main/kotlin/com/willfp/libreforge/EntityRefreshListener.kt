package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent

class EntityRefreshListener(
    private val plugin: EcoPlugin
) : Listener {

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        event.chunk.entities.filterIsInstance<LivingEntity>()
            .filterNot { it is Player }
            .forEach { entity ->
                removeEcoAttributeModifiers(entity)
            }
    }

    private fun removeEcoAttributeModifiers(entity: LivingEntity) {
        for (attribute in Attribute.entries) {
            val attributeInstance: AttributeInstance = entity.getAttribute(attribute) ?: continue

            attributeInstance.modifiers
                .filter { modifier -> modifier.name.matches(Regex("\\d+_\\d+")) }
                .forEach { modifier -> attributeInstance.removeModifier(modifier) }
        }
    }
}