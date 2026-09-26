package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object FilterBlockUsed : Filter<NoCompileData, Boolean>("block_used") {
    override val description = "Matches when the clicked block is (or is not) actually used by the interaction."
    override val categories = setOf("interaction")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf(
        "A block is not used when the player sneaks while holding an item in either hand, matching vanilla behaviour.",
        "Useful for harvesting sweet berry bushes or cave vines with click_block.",
        "Passes automatically when the event is not a block interaction."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event as? PlayerInteractEvent ?: return true
        val player = event.player
        val inventory = player.inventory

        val isSecondaryUse = player.isSneaking &&
                (!inventory.itemInMainHand.isEmpty || !inventory.itemInOffHand.isEmpty)

        val isUsed = event.action == Action.RIGHT_CLICK_BLOCK &&
                event.useInteractedBlock() != Event.Result.DENY &&
                !isSecondaryUse

        return isUsed == value
    }
}
