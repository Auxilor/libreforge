package com.willfp.libreforge.levels

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimpleInjectablePlaceholder
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.toNumeral
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.levels.event.ItemLevelUpEvent
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerLevelUpItem
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

class LevelType(
    override val id: String,
    config: Config,
    plugin: EcoPlugin
) : KRegistrable {
    private val xpFormula = config.getStringOrNull("xp-formula")

    private val maxLevel = config.getInt("max-level", Int.MAX_VALUE)

    private val requirements = config.getDoublesOrNull("requirements")

    private val levelUpEffects = Effects.compileChain(
        config.getSubsections("level-up-effects"),
        ViolationContext(plugin, "level $id level-up-effects")
    )

    fun getXPRequired(level: Int, context: PlaceholderContext): Double {
        if (level >= maxLevel) {
            return Double.MAX_VALUE
        }

        if (xpFormula != null) {
            return NumberUtils.evaluateExpression(
                xpFormula,
                context.withInjectableContext(
                    LevelInjectable(level)
                )
            )
        }

        if (requirements != null) {
            return requirements.getOrNull(level - 1) ?: Double.MAX_VALUE
        }

        throw IllegalStateException("Level $id has no requirements or xp formula")
    }

    fun handleLevelUp(level: Int, itemStack: ItemStack, context: PlaceholderContext) {
        val player = context.player ?: return

        Bukkit.getPluginManager().callEvent(ItemLevelUpEvent(player, itemStack, level, this))

        levelUpEffects?.trigger(
            DispatchedTrigger(
                PlayerDispatcher(player),
                TriggerLevelUpItem,
                TriggerData(
                    player = player,
                    item = itemStack,
                    value = level.toDouble(),
                    text = this.id
                )
            ).apply {
                addPlaceholder(NamedValue("level", level))
                addPlaceholder(NamedValue("level_numeral", level.toNumeral()))
            }
        )
    }

    private class LevelInjectable(
        private val level: Int
    ) : PlaceholderInjectable {
        private val injections = listOf(
            object : SimpleInjectablePlaceholder("level") {
                override fun getValue(p0: String, p1: PlaceholderContext): String =
                    level.toString()
            }
        )

        override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
            return injections
        }

        override fun clearInjectedPlaceholders() {
            // Do nothing.
        }

        override fun addInjectablePlaceholder(p0: Iterable<InjectablePlaceholder>) {
            // Do nothing.
        }
    }
}
