package com.willfp.libreforge.integrations.floodgate.impl

/**
 * Schema for a single button subsection of [EffectSendBedrockForm].
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 * Non-null properties are required keys; nullable properties are optional keys.
 *
 * @property text The label shown on the button.
 * @property commands The commands run as console when the button is clicked.
 * @property image_type The kind of image to show, either URL or PATH. Omit for no image.
 * @property image The image URL, or the Bedrock texture path when image_type is PATH.
 */
data class BedrockFormButtonSpec(
    val text: String,
    val commands: List<String>?,
    val image_type: List<String>? = listOf("URL", "PATH"),
    val image: String?
)
