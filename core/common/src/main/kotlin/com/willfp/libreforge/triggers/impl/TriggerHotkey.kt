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
abstract class TriggerHotkey(
    id: String,
    val hotkey: Hotkey,
    val isSneaking: Boolean
) : Trigger(id) {
    override val option: TriggerOption = TriggerOptions.create("triggers.hotkeys.$id.enabled")

    companion object {
        /**
         * One trigger for each hotkey, without and with sneaking.
         */
        val values: List<TriggerHotkey> = Hotkey.entries.flatMap { hotkey ->
            listOf(TriggerNormalHotkey(hotkey), TriggerShiftHotkey(hotkey))
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

class TriggerNormalHotkey internal constructor(
    hotkey: Hotkey
) : TriggerHotkey("hotkey_${hotkey.id}", hotkey, false) {
    override val description = "Fires when the player presses a hotkey while not sneaking."

    override val categories = setOf("hotkey")

    override val additionalInfo = listOf(
        "Replace <value> with the key: swap_hands (F by default), 1 to 9 (the hotbar keys), drop (Q by default, outside of an inventory) or advancements (L by default, opens the advancements screen).",
        "Requires triggers.hotkeys.hotkey_<key>.enabled to be set to true in libreforge's config.yml.",
        "While enabled, the key's normal action is always cancelled for every player who is not sneaking.",
        "The hotbar keys also fire when scrolling onto the slot, as the server cannot tell a number key apart from the scroll wheel.",
        "For drop, drops made by clicking in an inventory are ignored. This is detected on a best-effort basis.",
        "For advancements, the key is detected from the packet the client sends when the screen opens, so a modified client can skip it. Switching tabs while the screen is open does not fire it again."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.ITEM to "The item in the player's main hand for swap_hands and advancements, the item in the hotbar slot for 1 to 9, or the item that was dropped for drop."
    )

    override val parameters = setOfNotNull(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT.takeIf { hotkey.hasEvent },
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
}

class TriggerShiftHotkey internal constructor(
    hotkey: Hotkey
) : TriggerHotkey("shift_hotkey_${hotkey.id}", hotkey, true) {
    override val description = "Fires when the player presses a hotkey while sneaking."

    override val categories = setOf("hotkey")

    override val additionalInfo = listOf(
        "Replace <value> with the key: swap_hands (F by default), 1 to 9 (the hotbar keys), drop (Q by default, outside of an inventory) or advancements (L by default, opens the advancements screen).",
        "Requires triggers.hotkeys.shift_hotkey_<key>.enabled to be set to true in libreforge's config.yml.",
        "While enabled, the key's normal action is always cancelled for every player who is sneaking.",
        "The hotbar keys also fire when scrolling onto the slot, as the server cannot tell a number key apart from the scroll wheel.",
        "For drop, drops made by clicking in an inventory are ignored. This is detected on a best-effort basis.",
        "For advancements, the key is detected from the packet the client sends when the screen opens, so a modified client can skip it. Switching tabs while the screen is open does not fire it again."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.ITEM to "The item in the player's main hand for swap_hands and advancements, the item in the hotbar slot for 1 to 9, or the item that was dropped for drop."
    )

    override val parameters = setOfNotNull(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT.takeIf { hotkey.hasEvent },
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
}
