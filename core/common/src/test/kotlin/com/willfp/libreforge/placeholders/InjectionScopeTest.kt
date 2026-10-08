package com.willfp.libreforge.placeholders

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.libreforge.InjectionScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy
import java.util.regex.Pattern

class InjectionScopeTest {
    private class Value(private val name: String, private val value: String) : InjectablePlaceholder {
        override fun getPattern(): Pattern = Pattern.compile(name)

        override fun getValue(args: String, context: PlaceholderContext): String = value
    }

    private fun config(): Config =
        Proxy.newProxyInstance(Config::class.java.classLoader, arrayOf(Config::class.java)) { proxy, method, args ->
            when (method.name) {
                "hashCode" -> System.identityHashCode(proxy)
                "equals" -> proxy === args?.get(0)
                else -> throw UnsupportedOperationException(method.name)
            }
        } as Config

    private fun InjectionScope.valueOf(config: Config, name: String): String? =
        injectionsFor(config)[name]?.getValue(name, PlaceholderContext.EMPTY)

    @Test
    fun noScopeOutsideACall() {
        assertNull(InjectionScope.current())
    }

    @Test
    fun injectionsStayInTheirCall() {
        val shared = config()

        val first = InjectionScope.open {
            InjectionScope.current()!!.also { it.inject(shared, listOf(Value("level", "1"))) }
        }

        val second = InjectionScope.open {
            InjectionScope.current()!!.also { it.inject(shared, listOf(Value("level", "2"))) }
        }

        assertEquals("1", first.valueOf(shared, "level"))
        assertEquals("2", second.valueOf(shared, "level"))
        assertNull(InjectionScope.current())
    }

    @Test
    fun nestedCallsSeeTheirParentAndWinOverIt() {
        val shared = config()

        InjectionScope.open {
            val outer = InjectionScope.current()!!
            outer.inject(shared, listOf(Value("a", "outer"), Value("b", "outer")))

            InjectionScope.open {
                val inner = InjectionScope.current()!!
                inner.inject(shared, listOf(Value("b", "inner")))

                assertEquals("outer", inner.valueOf(shared, "a"))
                assertEquals("inner", inner.valueOf(shared, "b"))
            }

            assertEquals("outer", outer.valueOf(shared, "b"))
            assertTrue(InjectionScope.current() === outer)
        }
    }

    @Test
    fun subsectionsInheritWhatTheirParentHad() {
        val parent = config()
        val child = config()

        InjectionScope.open {
            val scope = InjectionScope.current()!!
            scope.inject(parent, listOf(Value("level", "3")))
            scope.inherit(parent, child)

            assertEquals("3", scope.valueOf(child, "level"))
        }
    }

    @Test
    fun mergeOverridesTheConfigsOwnInjections() {
        val shared = config()
        val base = listOf(Value("level", "base"), Value("other", "base"))

        InjectionScope.open {
            val scope = InjectionScope.current()!!
            scope.inject(shared, listOf(Value("level", "call")))

            val merged = scope.merge(shared, base).associate { it.patternString to it.getValue("", PlaceholderContext.EMPTY) }

            assertEquals(mapOf("other" to "base", "level" to "call"), merged)
        }
    }

    @Test
    fun aDelayedRunReentersItsCallOnAnotherThread() {
        val shared = config()

        val scope = InjectionScope.open {
            InjectionScope.current()!!.also { it.inject(shared, listOf(Value("level", "4"))) }
        }

        var seen: String? = null
        val thread = Thread {
            InjectionScope.enter(scope) {
                seen = InjectionScope.current()!!.valueOf(shared, "level")
            }
        }
        thread.start()
        thread.join()

        assertEquals("4", seen)
    }

    @Test
    fun callsOnDifferentThreadsDontSeeEachOther() {
        val shared = config()
        val seen = arrayOfNulls<String>(2)

        val threads = (0..1).map { index ->
            Thread {
                InjectionScope.open {
                    val scope = InjectionScope.current()!!
                    scope.inject(shared, listOf(Value("level", index.toString())))
                    Thread.sleep(20)
                    seen[index] = scope.valueOf(shared, "level")
                }
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        assertEquals(listOf("0", "1"), seen.toList())
    }
}
