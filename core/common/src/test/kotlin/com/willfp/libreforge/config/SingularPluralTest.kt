package com.willfp.libreforge.config

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.getStrings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy

class SingularPluralTest {
    private fun config(values: Map<String, Any>): Config =
        Proxy.newProxyInstance(Config::class.java.classLoader, arrayOf(Config::class.java)) { proxy, method, args ->
            val value = args?.firstOrNull()?.let { values[it] }
            when (method.name) {
                "hashCode" -> System.identityHashCode(proxy)
                "equals" -> proxy === args?.get(0)
                "getStringsOrNull" -> (value as? List<*>)?.filterIsInstance<String>()
                "getStrings" -> (value as? List<*>)?.filterIsInstance<String>().orEmpty()
                "getString" -> value?.let { "$it" }.orEmpty()
                else -> throw UnsupportedOperationException(method.name)
            }
        } as Config

    @Test
    fun singularKeyAsList() {
        val strings = config(mapOf("enchant" to listOf("sharpness:1-5", "unbreaking:1-3")))
            .getStrings(pluralPath = "enchants", singularPath = "enchant")

        assertEquals(listOf("sharpness:1-5", "unbreaking:1-3"), strings)
    }

    @Test
    fun singularKeyAsString() {
        assertEquals(
            listOf("sharpness:1-5"),
            config(mapOf("enchant" to "sharpness:1-5")).getStrings(pluralPath = "enchants", singularPath = "enchant")
        )
    }

    @Test
    fun pluralKeyAsList() {
        assertEquals(
            listOf("sharpness", "unbreaking", ""),
            config(mapOf("enchants" to listOf("sharpness", "unbreaking")))
                .getStrings(pluralPath = "enchants", singularPath = "enchant")
        )
    }

    @Test
    fun bothKeysSet() {
        val strings = config(mapOf("enchants" to listOf("sharpness"), "enchant" to listOf("unbreaking", "mending")))
            .getStrings(pluralPath = "enchants", singularPath = "enchant")

        assertEquals(listOf("sharpness", "unbreaking", "mending"), strings)
    }
}
