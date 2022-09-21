package com.willfp.libreforge.effects

/**
 * Requires an effect to run after other effects on the same trigger, if
 * possible.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RunLast
