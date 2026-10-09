package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ConcurrentHashMap

/**
 * The placeholders injected during one call, such as one effect being triggered.
 *
 * Compiled configs are shared by every dispatcher with the same holder, and on Folia they're read
 * on many regions at once, so placeholders injected during a call are kept here rather than on the
 * configs themselves, and are only seen by that call. Injections made outside any call still go to
 * the config.
 */
internal class InjectionScope private constructor(
    private val parent: InjectionScope?
) {
    private val injections: MutableMap<Config, MutableMap<String, InjectablePlaceholder>> =
        Collections.synchronizedMap(IdentityHashMap())

    /**
     * Inject [placeholders] into [config] for this call.
     */
    fun inject(config: Config, placeholders: Iterable<InjectablePlaceholder>) {
        val forConfig = injections.getOrPut(config) { ConcurrentHashMap() }

        for (placeholder in placeholders) {
            forConfig[placeholder.patternString] = placeholder
        }
    }

    /**
     * Give [child] the placeholders injected into [parent] so far, as reading a subsection does.
     */
    fun inherit(parent: Config, child: Config) {
        val inherited = injectionsFor(parent)

        if (inherited.isNotEmpty()) {
            inject(child, inherited.values)
        }
    }

    /**
     * The placeholders injected into [config] by this call and the calls it's nested in, by
     * pattern, with the innermost call winning.
     */
    fun injectionsFor(config: Config): Map<String, InjectablePlaceholder> {
        val own = injections[config]
        val outer = parent?.injectionsFor(config)

        return when {
            own == null -> outer ?: emptyMap()
            outer.isNullOrEmpty() -> own
            else -> outer + own
        }
    }

    /**
     * [base] with the placeholders injected into [config] by this call on top.
     */
    fun merge(config: Config, base: List<InjectablePlaceholder>): List<InjectablePlaceholder> {
        val injected = injectionsFor(config)

        if (injected.isEmpty()) {
            return base
        }

        return base.filter { it.patternString !in injected } + injected.values
    }

    companion object {
        private val current = ThreadLocal<InjectionScope?>()

        /**
         * The call running on this thread, if any.
         */
        fun current(): InjectionScope? = current.get()

        /**
         * Run [block] as a new call, nested in the current one if there is one.
         */
        fun <T> open(block: () -> T): T =
            enter(InjectionScope(current.get()), block)

        /**
         * Run [block] in an existing call, such as a delayed repeat of it.
         */
        fun <T> enter(scope: InjectionScope, block: () -> T): T {
            val previous = current.get()
            current.set(scope)

            try {
                return block()
            } finally {
                if (previous == null) {
                    current.remove()
                } else {
                    current.set(previous)
                }
            }
        }
    }
}
