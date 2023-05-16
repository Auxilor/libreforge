package com.willfp.libreforge.effects.arguments

import com.willfp.libreforge.Compilable
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.DispatchedTrigger

abstract class EffectArgument<T>(
    override val id: String
) : Compilable<T>() {
    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * Check if the argument is met.
     *
     * @param element The element.
     * @param trigger The trigger.
     * @param compileData The compile data.
     * @return If met.
     */
    open fun isMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger,
        compileData: T
    ) = true

    /**
     * If the argument is met, this will run.
     *
     * @param element The element.
     * @param trigger The trigger.
     * @param compileData The compile data.
     */
    open fun ifMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger,
        compileData: T
    ) = Unit

    /**
     * If the argument is not met, this will run.
     *
     * @param element The element.
     * @param trigger The trigger.
     * @param compileData The compile data.
     */
    open fun ifNotMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger,
        compileData: T
    ) = Unit

    /**
     * Check if the argument is met.
     *
     * @param element The element.
     * @param trigger The trigger.
     * @param compileData The compile data.
     * @return If met.
     */
    @Deprecated(
        "Use isMet(element, trigger, compileData)",
        ReplaceWith("isMet(element, trigger, compileData)"),
        DeprecationLevel.ERROR
    )
    open fun isMet(
        element: ElementLike,
        trigger: DispatchedTrigger,
        compileData: T
    ): Boolean = true

    /**
     * If the argument is met, this will run.
     *
     * @param element The element.
     * @param trigger The trigger.
     * @param compileData The compile data.
     */
    @Deprecated(
        "Use ifMet(element, trigger, compileData)",
        ReplaceWith("ifMet(element, trigger, compileData)"),
        DeprecationLevel.ERROR
    )
    open fun ifMet(
        element: ElementLike,
        trigger: DispatchedTrigger,
        compileData: T
    ) {
        // Override when needed.
    }

    /**
     * If the argument is not met, this will run.
     *
     * @param element The element.
     * @param trigger The trigger.
     * @param compileData The compile data.
     */
    @Deprecated(
        "Use ifMet(element, trigger, compileData)",
        ReplaceWith("ifMet(element, trigger, compileData)"),
        DeprecationLevel.ERROR
    )
    open fun ifNotMet(
        element: ElementLike,
        trigger: DispatchedTrigger,
        compileData: T
    ) {
        // Override when needed.
    }
}
