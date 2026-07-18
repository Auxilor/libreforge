package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.Material
import org.bukkit.entity.Player

object ConditionHasInventorySpace : Condition<NoCompileData>("has_inventory_space") {
    override val description = "Passes when the player has at least one free slot across the checked slot types."

    override val categories = setOf("player")

    override val arguments = arguments {
        optional(
            "include_armor_slots",
            description = "Whether to count empty armor slots as free space. Defaults to true.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "include_offhand",
            description = "Whether to count an empty offhand slot as free space. Defaults to true.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val inventory = player.inventory

        if (inventory.firstEmpty() != -1) return true

        val includeArmor = if (config.has("include_armor_slots")) config.getBool("include_armor_slots") else true
        if (includeArmor && inventory.armorContents.any { it == null || it.type == Material.AIR }) return true

        val includeOffhand = if (config.has("include_offhand")) config.getBool("include_offhand") else true
        if (includeOffhand && inventory.itemInOffHand.type == Material.AIR) return true

        return false
    }
}
