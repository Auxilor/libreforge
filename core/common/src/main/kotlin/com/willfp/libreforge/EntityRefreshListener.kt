package com.willfp.libreforge

import org.bukkit.Registry
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent

object EntityRefreshListener : Listener {

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        event.chunk.entities.filterIsInstance<LivingEntity>()
            .filterNot { it is Player }
            .forEach { entity ->
                removeEcoAttributeModifiers(entity)
            }
    }

    private fun removeEcoAttributeModifiers(entity: LivingEntity) {
        for (attribute in Registry.ATTRIBUTE) {
            val attributeInstance: AttributeInstance = entity.getAttribute(attribute) ?: continue

            @Suppress("USELESS_ELVIS")
            val modifiers = attributeInstance.modifiers ?: continue

            modifiers.filter { it.name.matches(Regex("\\d+_\\d+")) }
                .forEach { modifier ->
                    attributeInstance.removeModifier(modifier)
                }
        }
    }
}