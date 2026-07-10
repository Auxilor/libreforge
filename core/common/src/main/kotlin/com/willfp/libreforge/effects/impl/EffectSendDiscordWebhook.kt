package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.eco.core.integrations.discord.DiscordManager
import com.willfp.eco.core.integrations.discord.DiscordWebhookMessage
import com.willfp.eco.core.integrations.discord.DiscordEmbed
import com.willfp.eco.core.integrations.discord.DiscordEmbedFooter
import com.willfp.eco.core.integrations.discord.DiscordEmbedMedia
import com.willfp.eco.core.integrations.discord.DiscordEmbedAuthor
import com.willfp.eco.core.integrations.discord.DiscordEmbedField
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ConfigArguments
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData

object EffectSendDiscordWebhook: Effect<NoCompileData>("send_discord_webhook") {
    override val description = "Sends a message to a Discord channel via a webhook URL."
    override val categories = setOf("chat")

    override val arguments: ConfigArguments = arguments {
        require(
            "webhook_url",
            "You must specify the webhook URL!",
            description = "The Discord webhook URL to send the message to.",
            type = ArgType.STRING,
            example = "https://discord.com/api/webhooks/123456789012345678/abcDEF-token"
        )
        require(
            "text",
            "You must specify the text to send!",
            description = "The message content to send to the webhook.",
            type = ArgType.STRING,
            example = "%player_name% just found a legendary item!"
        )
        optional(
            "username",
            description = "The display name to use for the webhook message.",
            type = ArgType.STRING
        )
        optional(
            "avatar_url",
            description = "A URL to an image to use as the webhook avatar.",
            type = ArgType.STRING,
            example = "https://example.com/avatar.png"
        )
        optional(
            "tts",
            description = "Whether to send the message as text-to-speech.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "embeds",
            description = "A list of embed objects to include in the webhook message.",
            type = ArgType.ANY
        )
    }

    override val isPermanent = false

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val ctx = config.toPlaceholderContext(data)

        val webhookUrl = config.getFormattedString("webhook_url", ctx)

        val content = config.getFormattedString("text", ctx)

        val username = config.getFormattedStringOrNull("username", ctx)
        val avatarUrl = config.getStringOrNull("avatar_url")
        val tts = config.getBoolOrNull("tts") ?: false

        val embeds = (config.getSubsectionsOrNull("embeds") ?: emptyList()).map { buildEmbed(it, data) }

        val msg = DiscordWebhookMessage(content, username ?: "", avatarUrl ?: "", tts, embeds)

        return try {
            DiscordManager.executeWebhookJson(webhookUrl, msg, false)
            true
        } catch (e: Throwable) {
            plugin.logger.warning("Failed to send webhook webhook: ${e.message}")
            false
        }
    }
}

private fun buildEmbed(embedCfg: Config, data: TriggerData): DiscordEmbed {
    val ectx = embedCfg.toPlaceholderContext(data)

    val title = embedCfg.getFormattedStringOrNull("title", ectx)
    val description = embedCfg.getFormattedStringOrNull("description", ectx)
    val url = embedCfg.getStringOrNull("url")
    val color = embedCfg.getStringOrNull("color")?.removePrefix("#")?.toIntOrNull(16)
    val timestamp = embedCfg.getStringOrNull("timestamp")

    val authorName = embedCfg.getFormattedStringOrNull("author.name", ectx)
    val authorUrl = embedCfg.getStringOrNull("author.url")
    val authorIcon = embedCfg.getStringOrNull("author.icon_url")

    val footerText = embedCfg.getFormattedStringOrNull("footer.text", ectx)
    val footerIcon = embedCfg.getStringOrNull("footer.icon_url")

    val imageUrl = embedCfg.getStringOrNull("image.url")
    val thumbnailUrl = embedCfg.getStringOrNull("thumbnail.url")

    val fields = (embedCfg.getSubsectionsOrNull("fields") ?: emptyList()).map { f ->
        DiscordEmbedField(
            f.getFormattedStringOrNull("name", ectx) ?: "",
            f.getFormattedStringOrNull("value", ectx) ?: "",
            f.getBoolOrNull("inline") ?: false
        )
    }

    val footer = if (footerText != null || footerIcon != null) DiscordEmbedFooter(footerText, footerIcon) else null
    val image = if (imageUrl != null) DiscordEmbedMedia(imageUrl) else null
    val thumbnail = if (thumbnailUrl != null) DiscordEmbedMedia(thumbnailUrl) else null
    val author = if (authorName != null || authorUrl != null || authorIcon != null)
        DiscordEmbedAuthor(authorName, authorUrl, authorIcon) else null

    return DiscordEmbed(title, description, url, timestamp, color, footer, image, thumbnail, author,
        fields.ifEmpty { null })
}