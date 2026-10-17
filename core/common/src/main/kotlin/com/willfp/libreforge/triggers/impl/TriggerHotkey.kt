package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerOption
import com.willfp.libreforge.triggers.TriggerOptions
import com.willfp.libreforge.triggers.TriggerParameter

/**
 * `hotkey_<key>` and `shift_hotkey_<key>`, fired by [HotkeyListener].
 *
 * `hotkey_<key>` only fires while the player is not sneaking, and `shift_hotkey_<key>` only
 * while they are, so the two never overlap. Each is enabled by its own [option].
 */
class TriggerHotkey private constructor(
    val hotkey: Hotkey,
    val isSneaking: Boolean
) : Trigger(if (isSneaking) "shift_hotkey_${hotkey.id}" else "hotkey_${hotkey.id}") {
    override val option: TriggerOption = TriggerOptions.create("triggers.hotkeys.$id.enabled")

    override val description = "Fires when the player ${hotkey.action}" +
            if (isSneaking) " while sneaking." else " while not sneaking."

    override val categories = setOf("hotkey")

    override val additionalInfo = listOfNotNull(
        "Requires ${option.path} to be set to true in libreforge's config.yml.",
        "While enabled, the key's normal action is always cancelled for every player " +
                if (isSneaking) "who is sneaking." else "who is not sneaking.",
        hotkey.note
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.ITEM to hotkey.itemDescription
    )

    override val parameters = if (hotkey.hasEvent) {
        setOf(
            TriggerParameter.PLAYER,
            TriggerParameter.EVENT,
            TriggerParameter.LOCATION,
            TriggerParameter.ITEM
        )
    } else {
        setOf(
            TriggerParameter.PLAYER,
            TriggerParameter.LOCATION,
            TriggerParameter.ITEM
        )
    }

    companion object {
        /**
         * One trigger for each hotkey, without and with sneaking.
         */
        val values: List<TriggerHotkey> = Hotkey.entries.flatMap { hotkey ->
            listOf(TriggerHotkey(hotkey, false), TriggerHotkey(hotkey, true))
        }

        /**
         * The trigger for [hotkey] pressed while [isSneaking] or not.
         */
        fun of(hotkey: Hotkey, isSneaking: Boolean): TriggerHotkey =
            values[hotkey.ordinal * 2 + if (isSneaking) 1 else 0]

        /**
         * If the trigger for [hotkey] is enabled with or without sneaking.
         */
        fun isAnyEnabled(hotkey: Hotkey): Boolean =
            of(hotkey, false).option.isEnabled || of(hotkey, true).option.isEnabled
    }
}
