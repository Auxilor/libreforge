@file:Suppress("DEPRECATION")

package com.willfp.libreforge.integrations.paper.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.TeamUtils
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.ChainExecutors
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.scoreboard.Team

object EffectDropPickupItem : Effect<Chain?>("drop_pickup_item") {
    private const val META_KEY = "libreforge:pickup_item"

    override val parameters = setOf(
        TriggerParameter.LOCATION,
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("item", "You must specify the item to drop!")
        require("effects", "You must specify the effects to run on pickup!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: Chain?): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false
        val player = data.player ?: return false
        val chain = compileData ?: return false

        val glowColor = config.getStringOrNull("glow-color")
            ?.let { runCatching { ChatColor.valueOf(it.uppercase()) }.getOrNull() }

        val itemStack = Items.lookup(config.getString("item")).item

        val item = world.dropItemNaturally(location, itemStack)

        val meta = Meta(
            chain,
            data.dispatch(player.toDispatcher()),
            glowColor?.let { TeamUtils.fromChatColor(it) }
        )

        item.setMetadata(META_KEY, plugin.metadataValueFactory.create(meta))

        if (glowColor != null) {
            val team = TeamUtils.fromChatColor(glowColor)
            team.addEntry(item.uniqueId.toString())
            item.isGlowing = true
        }

        return true
    }

    @EventHandler
    fun onPickup(event: PlayerAttemptPickupItemEvent) {
        val item = event.item

        if (!item.hasMetadata(META_KEY)) {
            return
        }

        val meta = item.getMetadata(META_KEY).firstOrNull()?.value() as? Meta ?: return

        event.isCancelled = true
        item.remove()

        meta.chain.trigger(meta.trigger)
        meta.team?.removeEntry(item.uniqueId.toString())
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Chain? {
        return Effects.compileChain(
            config.getSubsections("effects"),
            ChainExecutors.getByID(config.getStringOrNull("run-type")),
            context.with("drop_pickup_entity effects")
        )
    }

    private data class Meta(
        val chain: Chain,
        val trigger: DispatchedTrigger,
        val team: Team?
    )
}
