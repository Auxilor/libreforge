package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.concurrentListMap
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectInfiniteBucket : Effect<Set<String>>("infinite_bucket") {
    override val description = "Prevents the specified bucket types from being emptied, refilling them automatically."
    override val categories = setOf("player", "world")

    override val arguments = arguments {
        require(
            listOf("type", "types"),
            "You must specify the bucket type(s) (e.g. type: any, or types: [lava, water, axolotl, etc.])!",
            description = "The bucket type(s) to make infinite. Use 'any' to apply to all bucket types.",
            type = ArgType.STRING_LIST
        )
    }

    private val activePlayers = concurrentListMap<UUID, Pair<UUID, Set<String>>>()

    private fun Player.isInfinite(type: String): Boolean {
        val active = activePlayers[this.uniqueId]
        return active.any { (_, types) -> types.isEmpty() || type in types }
    }

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        val player = event.player
        if (!player.isInfinite(event.bucket.name)) return

        event.itemStack = ItemStack(event.bucket)
    }

    @EventHandler
    fun onMilkConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        if (event.item.type != Material.MILK_BUCKET) return
        if (!player.isInfinite("MILK_BUCKET")) return

        event.replacement = ItemStack(Material.MILK_BUCKET)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Set<String> {
        val types = config.getStrings("types", "type")
        return if (types.size == 1 && types[0].equals("any", ignoreCase = true)) {
            emptySet()
        } else {
            types.map { it.uppercase() + "_BUCKET" }.toSet()
        }
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: Set<String>
    ) {
        val player = dispatcher.get<Player>() ?: return
        activePlayers[player.uniqueId].add(identifiers.uuid to compileData)
    }

    override fun onDisable(
        dispatcher: Dispatcher<*>,
        identifiers: Identifiers,
        holder: ProvidedHolder
    ) {
        val player = dispatcher.get<Player>() ?: return
        activePlayers[player.uniqueId].removeIf { it.first == identifiers.uuid }
    }
}
