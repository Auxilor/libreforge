package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import org.bukkit.entity.Item

object EffectItemMagnet : Effect<EffectItemMagnet.ItemMagnetFilter>("item_magnet") {
    override val description = "Pulls nearby dropped items toward the holder while active, optionally restricted to specific item types."
    override val categories = setOf("inventory", "movement")

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius within which dropped items will be pulled, in blocks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "5 + %level% * 0.5"
        )
        optional(
            "items",
            description = "A whitelist of item types to attract. If empty, all items are attracted.",
            type = ArgType.ITEM_LIST,
            default = "[]"
        )
        optional(
            "exclude_items",
            description = "A blacklist of item types to never attract, checked after the whitelist.",
            type = ArgType.ITEM_LIST,
            default = "[]"
        )
        optional(
            "pull_strength",
            description = "How strongly items are pulled per tick. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "0.3",
            example = "0.1 + %level% * 0.02"
        )
    }

    private val tasks = mutableMapOf<Identifiers, RunnableTask>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: ItemMagnetFilter
    ) {
        val radius = config.getDoubleFromExpression("radius", dispatcher.get())
        val pullStrength = config.getOrNull("pull_strength") { getDoubleFromExpression(it, dispatcher.get()) } ?: 0.3

        val runnable = plugin.runnableFactory.create { _ ->
            val location = dispatcher.location ?: return@create
            val world = location.world ?: return@create

            for (entity in world.getNearbyEntities(location, radius, radius, radius)) {
                val item = entity as? Item ?: continue
                val stack = item.itemStack

                if (compileData.whitelist.isNotEmpty() && compileData.whitelist.none { it.matches(stack) }) {
                    continue
                }

                if (compileData.blacklist.any { it.matches(stack) }) {
                    continue
                }

                val pull = location.toVector().subtract(item.location.toVector())

                if (pull.lengthSquared() < 1e-6) {
                    continue
                }

                item.velocity = item.velocity.add(pull.normalize().multiply(pullStrength))
            }
        }

        runnable.runTaskTimer(0L, 1L)
        tasks[identifiers] = runnable
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        tasks.remove(identifiers)?.cancel()
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ItemMagnetFilter {
        return ItemMagnetFilter(
            whitelist = config.getStrings("items").map { Items.lookup(it) },
            blacklist = config.getStrings("exclude_items").map { Items.lookup(it) }
        )
    }

    data class ItemMagnetFilter(
        val whitelist: List<TestableItem>,
        val blacklist: List<TestableItem>
    )
}
