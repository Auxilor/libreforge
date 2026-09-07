package com.willfp.libreforge.levels

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimpleInjectablePlaceholder
import com.willfp.eco.core.progression.LevelCurve
import com.willfp.eco.core.progression.LevelCurves
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.levels.event.ItemLevelUpEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerLevelUpItem
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

class LevelType(
    override val id: String,
    config: Config,
    plugin: EcoPlugin
) : KRegistrable {
    private val parsedCurve = LevelCurves.parse(
        config.getStringOrNull("xp-formula"),
        config.getDoublesOrNull("requirements"),
        config.getIntOrNull("max-level"),
        // Item levels start at 1: LevelData's base is LevelData(1, 0.0), and today's
        // getXPRequired(level) indexes requirements[level - 1].
        startLevel = 1,
        freeFirstLevel = false
    ) { expression, level ->
        NumberUtils.evaluateExpression(
            expression,
            PlaceholderContext.EMPTY.withInjectableContext(LevelInjectable(level - 1))
        )
    }

    val curve: LevelCurve = parsedCurve.curve

    private val levelUpEffects = Effects.compileChain(
        config.getSubsections("level-up-effects"),
        ViolationContext(plugin, "level $id level-up-effects")
    )

    init {
        for (problem in parsedCurve.problems) {
            plugin.logger.warning("Level type $id: ${problem.path} - ${problem.message}")
        }
    }

    /**
     * The XP needed to go from [level] to [level] + 1.
     *
     * Previously threw IllegalStateException when neither key was configured, from a path
     * reached on every XP gain. A misconfigured level type now simply stops advancing.
     */
    @Suppress("UNUSED_PARAMETER") // Kept for source compatibility - the curve captures its own evaluator.
    fun getXPRequired(level: Int, context: PlaceholderContext): Double =
        curve.xpToReach(level + 1)

    fun handleLevelUp(level: Int, itemStack: ItemStack, context: PlaceholderContext) {
        val player = context.player ?: return

        Bukkit.getPluginManager().callEvent(ItemLevelUpEvent(player, itemStack, level, this))

        // Shared so %level% and its siblings mean the same thing in every level-up-effects
        // block. This gains %previous_level% and %previous_level_numeral%, which item levels
        // did not previously offer; the trigger is deliberately not dispatched globally,
        // matching what this block did before.
        LevelUpDispatcher.dispatch(
            player.toDispatcher(),
            TriggerLevelUpItem,
            levelUpEffects,
            level,
            TriggerData(
                player = player,
                item = itemStack,
                value = level.toDouble(),
                text = this.id
            ),
            type = "level"
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
