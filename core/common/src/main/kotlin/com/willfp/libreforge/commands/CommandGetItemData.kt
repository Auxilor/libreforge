package com.willfp.libreforge.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.asAudience
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.plugin
import com.willfp.libreforge.slot.SlotTypes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

internal object CommandGetItemData : Subcommand(
    plugin,
    "getitemdata",
    "libreforge.command.getitemdata",
    false
) {
    @Suppress("UsagesOfObsoleteApi")
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val playerName = args.getOrNull(0)

        if (playerName == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-player"))
            return
        }

        val player = Bukkit.getPlayer(playerName)

        if (player == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        val slotString = args.getOrNull(1)

        if (slotString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-slot"))
            return
        }

        val slotType = SlotTypes[slotString.lowercase()]

        if (slotType == null) {
            sender.sendMessage(
                plugin.langYml.getMessage("invalid-slot")
                    .replace("%slot%", slotString)
            )
            return
        }

        val items = slotType.getItemSlots(player)
            .distinct()
            .sorted()
            .map { slot -> slot to player.inventory.getItem(slot) }
            .filterNot { (_, item) -> item.isEcoEmpty }

        if (items.isEmpty()) {
            sender.sendMessage(
                plugin.langYml.getMessage("empty-slot")
                    .replace("%player%", player.name)
                    .replace("%slot%", slotString)
            )
            return
        }

        sender.sendMessage(
            plugin.langYml.getMessage("item-data-header")
                .replace("%player%", player.name)
                .replace("%slot%", slotType.id)
        )

        val audience = sender.asAudience()

        for ((slot, item) in items) {
            val compactSnbt = Items.toSNBT(item!!)
                .removeRootField("DataVersion")
                .compactOutsideStrings()
            val escapedSnbt = compactSnbt.escapeAsJsonStringLiteral()

            audience.sendMessage(
                Component.text("[$slot] ", NamedTextColor.GRAY)
                    .append(
                        Component.text(escapedSnbt, NamedTextColor.YELLOW)
                            .clickEvent(ClickEvent.copyToClipboard(escapedSnbt))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.GRAY)))
                    )
            )
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                mutableListOf()
            )

            2 -> StringUtil.copyPartialMatches(
                args[1],
                COMMON_SLOT_SUGGESTIONS,
                mutableListOf()
            )

            else -> emptyList()
        }
    }
}

private val COMMON_SLOT_SUGGESTIONS = listOf(
    "mainhand",
    "offhand",
    "helmet",
    "chestplate",
    "leggings",
    "boots",
)

private fun String.compactOutsideStrings(): String {
    val result = StringBuilder(this.length)
    var inString = false
    var escaping = false

    for (character in this) {
        if (inString) {
            result.append(character)

            if (escaping) {
                escaping = false
                continue
            }

            when (character) {
                '\\' -> escaping = true
                '"' -> inString = false
            }

            continue
        }

        when (character) {
            '"' -> {
                inString = true
                result.append(character)
            }

            ' ', '\n', '\r', '\t' -> {
                // Drop formatting whitespace outside strings so SNBT stays one-line.
            }

            else -> result.append(character)
        }
    }

    return result.toString()
}

private fun String.escapeAsJsonStringLiteral(): String {
    val result = StringBuilder(this.length + 2)
    result.append('"')

    for (character in this) {
        when (character) {
            '\\' -> result.append("\\\\")
            '"' -> result.append("\\\"")
            '\b' -> result.append("\\b")
            '\u000C' -> result.append("\\f")
            '\n' -> result.append("\\n")
            '\r' -> result.append("\\r")
            '\t' -> result.append("\\t")
            else -> {
                if (character.code < 0x20) {
                    val hex = character.code.toString(16).padStart(4, '0')
                    result.append("\\u").append(hex)
                } else {
                    result.append(character)
                }
            }
        }
    }

    result.append('"')
    return result.toString()
}

private fun String.removeRootField(fieldName: String): String {
    val trimmed = this.trim()

    if (!trimmed.startsWith('{') || !trimmed.endsWith('}')) {
        return trimmed
    }

    val entries = mutableListOf<String>()
    val current = StringBuilder()
    var depth = 0
    var inString = false
    var escaping = false

    for (index in 1 until trimmed.length - 1) {
        val character = trimmed[index]

        if (inString) {
            current.append(character)

            if (escaping) {
                escaping = false
                continue
            }

            when (character) {
                '\\' -> escaping = true
                '"' -> inString = false
            }

            continue
        }

        when (character) {
            '"' -> {
                inString = true
                current.append(character)
            }

            '{', '[', '(' -> {
                depth++
                current.append(character)
            }

            '}', ']', ')' -> {
                depth--
                current.append(character)
            }

            ',' -> {
                if (depth == 0) {
                    current.toString().trim().takeIf { it.isNotEmpty() }?.let(entries::add)
                    current.setLength(0)
                } else {
                    current.append(character)
                }
            }

            else -> current.append(character)
        }
    }

    current.toString().trim().takeIf { it.isNotEmpty() }?.let(entries::add)

    return entries
        .filterNot { entry ->
            val colonIndex = entry.indexOf(':')
            if (colonIndex == -1) {
                false
            } else {
                entry.substring(0, colonIndex).trim().removeSurrounding("\"") == fieldName
            }
        }
        .joinToString(prefix = "{", postfix = "}", separator = ", ")
}
