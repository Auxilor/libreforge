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
 * Four plugins each hand-rolled this block with slightly different placeholder sets; sharing
 * it is what makes `%level%` and `%previous_level%` mean the same thing everywhere. Call it
 * once per level in a [com.willfp.eco.core.progression.LevelChange.levelsGained] range - not
 * once per grant - so a multi-level gain does not swallow the rewards in between.
 */
object LevelUpDispatcher {
    @JvmStatic
    fun dispatch(
        dispatcher: Dispatcher<*>,
        trigger: Trigger,
        chain: Chain?,
        level: Int,
        data: TriggerData
    ) {
        val dispatched = DispatchedTrigger(dispatcher, trigger, data).apply {
            addPlaceholder(NamedValue("level", level))
            addPlaceholder(NamedValue("level_numeral", level.toNumeral()))
            addPlaceholder(NamedValue("previous_level", level - 1))
        }

        trigger.dispatch(dispatcher, dispatched.data)
        chain?.trigger(dispatched)
    }
}
