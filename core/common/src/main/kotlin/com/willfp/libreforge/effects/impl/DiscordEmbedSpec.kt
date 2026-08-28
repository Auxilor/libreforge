package com.willfp.libreforge.effects.impl

/**
 * Schema for a single embed subsection of [EffectSendDiscordWebhook].
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 * Non-null properties are required keys; nullable properties are optional keys.
 *
 * @property title The embed title, shown in bold at the top of the embed. Supports placeholders.
 * @property description The embed body text. Supports placeholders.
 * @property url A URL to make the title a clickable link to. Does not support placeholders.
 * @property color The embed sidebar color as a hex string, with or without a leading hash.
 * @property timestamp An ISO-8601 timestamp to show in the embed footer, e.g. 2026-01-01T12:00:00Z.
 * @property author The author line shown above the title.
 * @property footer The footer line shown below the embed body.
 * @property image The large image shown at the bottom of the embed.
 * @property thumbnail The small image shown in the top right of the embed.
 * @property fields A list of name-value fields shown in the embed body.
 */
data class DiscordEmbedSpec(
    val title: String?,
    val description: String?,
    val url: String?,
    val color: String?,
    val timestamp: String?,
    val author: DiscordEmbedAuthorSpec?,
    val footer: DiscordEmbedFooterSpec?,
    val image: DiscordEmbedMediaSpec?,
    val thumbnail: DiscordEmbedMediaSpec?,
    val fields: List<DiscordEmbedFieldSpec>?,
)

/**
 * Schema for the author subsection of a [DiscordEmbedSpec].
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 *
 * @property name The author name shown above the embed title. Supports placeholders.
 * @property url A URL to make the author name a clickable link to. Does not support placeholders.
 * @property iconUrl A URL to an image to show as the author icon. Does not support placeholders.
 */
data class DiscordEmbedAuthorSpec(
    val name: String?,
    val url: String?,
    val iconUrl: String?,
)

/**
 * Schema for the footer subsection of a [DiscordEmbedSpec].
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 *
 * @property text The footer text shown below the embed body. Supports placeholders.
 * @property iconUrl A URL to an image to show as the footer icon. Does not support placeholders.
 */
data class DiscordEmbedFooterSpec(
    val text: String?,
    val iconUrl: String?,
)

/**
 * Schema for the image and thumbnail subsections of a [DiscordEmbedSpec].
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 *
 * @property url A URL to the image to show. Does not support placeholders.
 */
data class DiscordEmbedMediaSpec(
    val url: String,
)

/**
 * Schema for a single entry in the fields list of a [DiscordEmbedSpec].
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 *
 * @property name The field name, shown in bold. Supports placeholders.
 * @property value The field value, shown below the name. Supports placeholders.
 * @property inline Whether to show this field on the same line as adjacent inline fields.
 */
data class DiscordEmbedFieldSpec(
    val name: String?,
    val value: String?,
    val inline: Boolean?,
)
