package com.willfp.libreforge.levels

import com.willfp.eco.util.toNumeral
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData

/**
 * Fires a level-up effect chain for one level.
 *
 * Several levelling systems each hand-rolled this block with slightly different placeholder
 * sets; sharing it is what makes `%level%` and `%previous_level%` mean the same thing
 * everywhere. Call it once per level in a
 * [com.willfp.eco.core.progression.LevelChange.levelsGained] range - not once per grant - so a
 * multi-level gain does not swallow the rewards in between.
 *
 * ## The placeholder set is the union of what the call sites had, never a subset
 *
 * The hand-rolled blocks did not agree on which placeholders they offered, and a server
 * owner's config is written against whichever one their system used. So this provides every
 * placeholder any of them had. Unifying downward would silently delete a placeholder from
 * configs that use it, rendering it as literal text. Adding a placeholder a call site did not
 * previously have is safe; removing one is a config break, so this set only ever grows.
 */
object LevelUpDispatcher {
    /**
     * @param dispatcher      Who levelled up.
     * @param trigger         The level-up trigger for this system.
     * @param chain           The configured `level-up-effects`, or null.
     * @param level           The level just reached.
     * @param data            Trigger data for this system.
     * @param dispatchTrigger Whether to also dispatch [trigger] globally, so that unrelated
     *                        effect holders listening for it fire too. **Defaults to false,
     *                        because most call sites did not do this** - they trigger their
     *                        own chain and nothing else. Switching them on by default would
     *                        start firing every player's effects on a trigger that has never
     *                        fired for them before, which is a behaviour change dressed up as
     *                        a refactor.
     */
    @JvmStatic
    @JvmOverloads
    fun dispatch(
        dispatcher: Dispatcher<*>,
        trigger: Trigger,
        chain: Chain?,
        level: Int,
        data: TriggerData,
        dispatchTrigger: Boolean = false
    ) {
        val dispatched = DispatchedTrigger(dispatcher, trigger, data).apply {
            addPlaceholder(NamedValue("level", level))
            addPlaceholder(NamedValue("level_numeral", level.toNumeral()))
            addPlaceholder(NamedValue("previous_level", level - 1))
            addPlaceholder(NamedValue("previous_level_numeral", (level - 1).toNumeral()))
        }

        if (dispatchTrigger) {
            trigger.dispatch(dispatcher, dispatched.data)
        }

        chain?.trigger(dispatched)
    }
}
