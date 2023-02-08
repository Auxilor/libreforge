package com.willfp.libreforge.triggers

abstract class TriggerGroup(
    val prefix: String
) {
    abstract fun create(value: String): Trigger?
}
