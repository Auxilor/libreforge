package com.willfp.libreforge.integrations.floodgate.impl

/**
 * Schema for a single component subsection of [EffectSendBedrockCustomForm].
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 * Non-null properties are required keys; nullable properties are optional keys.
 *
 * @property type The kind of component to add.
 * @property text The label shown next to the component.
 * @property placeholder Greyed out hint text shown in an empty input. Inputs only.
 * @property default The starting value: the text of an input, true or false for a toggle, a
 * number for a slider, or a zero-based option index for a dropdown or step slider.
 * @property options The options to choose between. Dropdowns and step sliders only.
 * @property min The lowest value of a slider.
 * @property max The highest value of a slider.
 * @property step How far a slider moves per notch.
 */
data class BedrockFormComponentSpec(
    val type: List<String> = listOf("label", "input", "toggle", "slider", "dropdown", "step_slider"),
    val text: String,
    val placeholder: String?,
    val default: String?,
    val options: List<String>?,
    val min: Double?,
    val max: Double?,
    val step: Double?
)
