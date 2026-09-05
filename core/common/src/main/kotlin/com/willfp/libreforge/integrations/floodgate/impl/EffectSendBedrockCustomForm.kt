package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.integrations.floodgate.BedrockFormEvent
import com.willfp.libreforge.integrations.floodgate.BedrockFormType
import com.willfp.libreforge.integrations.floodgate.floodgate
import com.willfp.libreforge.integrations.floodgate.handleFormResult
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.cumulus.response.CustomFormResponse

/**
 * A component on a [EffectSendBedrockCustomForm], as parsed from config.
 */
sealed interface BedrockFormComponent {
    /** The label shown next to the component. */
    val text: String

    /** Text that does not accept any input. */
    data class Label(override val text: String) : BedrockFormComponent

    /** A single line text box. */
    data class Input(
        override val text: String,
        val placeholder: String,
        val default: String
    ) : BedrockFormComponent

    /** An on/off switch. */
    data class Toggle(
        override val text: String,
        val default: Boolean
    ) : BedrockFormComponent

    /** A numeric slider. */
    data class Slider(
        override val text: String,
        val min: Float,
        val max: Float,
        val step: Float,
        val default: Float
    ) : BedrockFormComponent

    /** A drop down list of options. */
    data class Dropdown(
        override val text: String,
        val options: List<String>,
        val default: Int
    ) : BedrockFormComponent

    /** A slider that snaps between named options. */
    data class StepSlider(
        override val text: String,
        val options: List<String>,
        val default: Int
    ) : BedrockFormComponent
}

object EffectSendBedrockCustomForm : Effect<List<BedrockFormComponent>>("send_bedrock_custom_form") {
    override val description = "Shows the player a native Bedrock form of inputs, toggles, sliders, and dropdowns."

    override val categories = setOf("chat", "player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Does nothing for Java players, who have no way to display a Bedrock form.",
        "Submitting the form runs the commands, then fires the bedrock_form_response trigger.",
        "Answers are available to the commands as %answer_1%, %answer_2%, and so on, numbered in config order and skipping labels.",
        "The trigger's text is the first answer; read the rest from the BedrockFormEvent in your own code.",
        "Closing the form fires the bedrock_form_closed trigger instead, and runs nothing.",
        "Set form_id and filter on it with bedrock_form_id so your chains can tell forms apart."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "title",
            "You must specify the form title!",
            description = "The title shown at the top of the form.",
            type = ArgType.STRING,
            example = "&aApply for staff"
        )
        optional(
            "form_id",
            description = "An ID for this form, matched by the bedrock_form_id filter on the response triggers.",
            type = ArgType.STRING,
            default = "",
            example = "staff_application"
        )
        require(
            "components",
            "You must specify the components!",
            description = "The components on the form, in order.",
            type = ArgType.DYNAMIC,
            schema = BedrockFormComponentSpec::class
        )
        optional(
            listOf("commands", "command"),
            description = "The commands run as console on submission. Use %answer_1%, %answer_2%, and so on for the answers.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("staffapp submit %player% %answer_1%")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: List<BedrockFormComponent>): Boolean {
        val player = data.player ?: return false

        if (!isBedrockPlayer(player) || compileData.isEmpty()) {
            return false
        }

        val context = config.toPlaceholderContext(data)
        val formId = config.getString("form_id")
        val commands = config.getStrings("commands", "command")

        val builder = CustomForm.builder()
            .title(config.getString("title").translatePlaceholders(context))

        for (component in compileData) {
            val label = component.text.translatePlaceholders(context)

            when (component) {
                is BedrockFormComponent.Label -> builder.label(label)
                is BedrockFormComponent.Input -> builder.input(label, component.placeholder, component.default)
                is BedrockFormComponent.Toggle -> builder.toggle(label, component.default)
                is BedrockFormComponent.Slider ->
                    builder.slider(label, component.min, component.max, component.step, component.default)
                is BedrockFormComponent.Dropdown -> builder.dropdown(label, component.options, component.default)
                is BedrockFormComponent.StepSlider -> builder.stepSlider(label, component.options, component.default)
            }
        }

        builder.validResultHandler { response ->
            val answers = readAnswers(response, compileData)

            handleFormResult(
                BedrockFormEvent(
                    formId = formId,
                    formType = BedrockFormType.CUSTOM,
                    player = player,
                    isClosed = false,
                    inputs = answers
                ),
                commands.map { it.withAnswers(answers) },
                config,
                data
            )
        }

        builder.closedOrInvalidResultHandler { ->
            handleFormResult(
                BedrockFormEvent(
                    formId = formId,
                    formType = BedrockFormType.CUSTOM,
                    player = player,
                    isClosed = true
                ),
                emptyList(),
                config,
                data
            )
        }

        return floodgate.sendForm(player.uniqueId, builder.build())
    }

    /**
     * Read every answer out of a response as a string.
     *
     * Cumulus indexes values by absolute component position, labels included, so the answer
     * for a component is read at the index that component sits at in config. Labels have no
     * answer and are left out of the result entirely, which is what makes %answer_1% the
     * first thing the player actually filled in.
     *
     * Dropdowns and step sliders answer with the index of the chosen option; that is mapped
     * back to the option text, as an index is meaningless in a command.
     */
    private fun readAnswers(
        response: CustomFormResponse,
        components: List<BedrockFormComponent>
    ): List<String> {
        val answers = mutableListOf<String>()

        for ((index, component) in components.withIndex()) {
            if (component is BedrockFormComponent.Label) {
                continue
            }

            val raw = runCatching { response.valueAt<Any?>(index) }.getOrNull()

            val answer = when (component) {
                is BedrockFormComponent.Label -> null
                is BedrockFormComponent.Input -> raw as? String
                is BedrockFormComponent.Toggle -> (raw as? Boolean)?.toString()
                is BedrockFormComponent.Slider -> (raw as? Number)?.toNiceString()
                is BedrockFormComponent.Dropdown ->
                    (raw as? Number)?.let { component.options.getOrNull(it.toInt()) }
                is BedrockFormComponent.StepSlider ->
                    (raw as? Number)?.let { component.options.getOrNull(it.toInt()) }
            }

            answers += answer ?: ""
        }

        return answers
    }

    /**
     * Format a slider value without a trailing .0, so whole numbers read as whole numbers
     * when they land in a command.
     */
    private fun Number.toNiceString(): String {
        val value = this.toDouble()

        return if (value == Math.floor(value) && !value.isInfinite()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }

    /**
     * Substitute %answer_n% placeholders, numbered from one.
     */
    private fun String.withAnswers(answers: List<String>): String {
        var result = this

        for ((index, answer) in answers.withIndex()) {
            result = result.replace("%answer_${index + 1}%", answer)
        }

        return result
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<BedrockFormComponent> {
        return config.getSubsections("components").mapNotNull { section ->
            val text = section.getStringOrNull("text") ?: ""

            when (section.getString("type").lowercase()) {
                "label" -> BedrockFormComponent.Label(text)

                "input" -> BedrockFormComponent.Input(
                    text,
                    section.getStringOrNull("placeholder") ?: "",
                    section.getStringOrNull("default") ?: ""
                )

                "toggle" -> BedrockFormComponent.Toggle(
                    text,
                    section.getBool("default")
                )

                "slider" -> BedrockFormComponent.Slider(
                    text,
                    section.getDouble("min").toFloat(),
                    section.getDouble("max").toFloat(),
                    (section.getDoubleOrNull("step") ?: 1.0).toFloat(),
                    (section.getDoubleOrNull("default") ?: section.getDouble("min")).toFloat()
                )

                "dropdown" -> BedrockFormComponent.Dropdown(
                    text,
                    section.getStrings("options", "option"),
                    section.getIntOrNull("default") ?: 0
                )

                "step_slider" -> BedrockFormComponent.StepSlider(
                    text,
                    section.getStrings("options", "option"),
                    section.getIntOrNull("default") ?: 0
                )

                else -> null
            }
        }
    }
}
