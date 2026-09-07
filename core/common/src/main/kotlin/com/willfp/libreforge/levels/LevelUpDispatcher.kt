package com.willfp.libreforge.levels

import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.DynamicInjectablePlaceholder
import com.willfp.eco.core.progression.ProgressionPlaceholders
import com.willfp.eco.util.toNumeral
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData

/**
 * Fires a progression effect chain for one step of progress.
 *
 * Several systems each hand-rolled this block with slightly different placeholder sets;
 * sharing it is what makes `%level%` and its siblings mean the same thing everywhere. Call it
 * once per step in a [com.willfp.eco.core.progression.LevelChange.levelsGained] range - not
 * once per grant - so a multi-level gain does not swallow the rewards in between.
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
     * Dispatch a progression step.
     *
     * Provides, where `type` is the progression's own word for a step:
     *
     * | Placeholder | Value |
     * |---|---|
     * | `%<type>%` | [value] |
     * | `%<type>_numeral%` | [value] as a Roman numeral |
     * | `%previous_<type>%` | [value] - 1 |
     * | `%previous_<type>_numeral%` | [value] - 1 as a Roman numeral |
     * | `%<type>_N%` | [value] + N, for any integer N (`%level_2%`, `%level_-1%`) |
     * | `%<type>_N_numeral%` | the same, as a Roman numeral |
     *
     * @param dispatcher      Who progressed.
     * @param trigger         The progression trigger for this system.
     * @param chain           The configured effect chain, or null.
     * @param value           The level, tier, or rank just reached.
     * @param data            Trigger data for this system.
     * @param dispatchTrigger Whether to also dispatch [trigger] globally, so that unrelated
     *                        effect holders listening for it fire too. **Defaults to false,
     *                        because most call sites did not do this** - they trigger their
     *                        own chain and nothing else. Switching them on by default would
     *                        start firing every player's effects on a trigger that has never
     *                        fired for them before, which is a behaviour change dressed up as
     *                        a refactor.
     * @param type            The word this progression uses for a step, and therefore the
     *                        placeholder prefix. Defaults to `level`; pass `tier` for a tier
     *                        ladder, and so on. It must match the vocabulary the server owner
     *                        already reads in their config keys and docs - a tier system that
     *                        suddenly wanted `%level%` in its reward block would be worse than
     *                        no sharing at all.
     */
    @JvmStatic
    @JvmOverloads
    fun dispatch(
        dispatcher: Dispatcher<*>,
        trigger: Trigger,
        chain: Chain?,
        value: Int,
        data: TriggerData,
        dispatchTrigger: Boolean = false,
        type: String = "level"
    ) {
        val dispatched = DispatchedTrigger(dispatcher, trigger, data).apply {
            addPlaceholder(NamedValue(type, value))
            addPlaceholder(NamedValue("${type}_numeral", value.toNumeral()))
            addPlaceholder(NamedValue("previous_$type", value - 1))
            addPlaceholder(NamedValue("previous_${type}_numeral", (value - 1).toNumeral()))
            addPlaceholder(RelativeValue(type, value))
        }

        if (dispatchTrigger) {
            trigger.dispatch(dispatcher, dispatched.data)
        }

        chain?.trigger(dispatched)
    }

    /**
     * `%<type>_N%` and `%<type>_N_numeral%`, resolving to [value] offset by `N`.
     *
     * Several plugins already offer this in lore and messages, each with its own copy of the
     * same regex, but none of them offered it inside an effect chain - so a `level-up-effects`
     * block could say `%level%` but not `%level_1%`, while the lore two lines away could do
     * both. This closes that gap in the one place every progression now goes through.
     *
     * A fixed [NamedValue] cannot express this, because `N` is unbounded; it needs a pattern.
     * Note the pattern deliberately carries no `%` delimiters: eco matches injected
     * placeholders against the text *between* the delimiters.
     */
    private class RelativeValue(
        private val type: String,
        private val value: Int
    ) : NamedValue(listOf("${type}_relative"), value) {
        private val placeholder = object : DynamicInjectablePlaceholder(
            ProgressionPlaceholders.offsetPattern(type)
        ) {
            // Resolution lives in eco alongside the lore and message path, so an effect chain
            // and a lore line can never disagree about what %level_2% means.
            override fun getValue(args: String, context: PlaceholderContext): String? =
                ProgressionPlaceholders.resolveOffset(args, type, value)
        }

        override val placeholders: List<InjectablePlaceholder> = listOf(placeholder)
    }
}
