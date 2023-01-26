package com.willfp.libreforge

import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.EffectList
import org.bukkit.NamespacedKey

/**
 * A holder 'holds' a list of effects and a list of conditions.
 * This is essentially the core of libreforge, and implementations
 * are things like Talismans, Enchantment Levels, Items, etc.
 */
interface Holder {
    /**
     * The ID of the holder, must be unique.
     */
    val id: NamespacedKey

    /**
     * The effects.
     */
    val effects: EffectList

    /**
     * The conditions.
     */
    val conditions: ConditionList
}
