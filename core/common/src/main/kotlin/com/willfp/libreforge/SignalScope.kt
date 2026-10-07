package com.willfp.libreforge

/**
 * What part of a dispatcher a change signal touched. A signal without a scope touches everything,
 * and a consumer that does not know a scope must treat it the same way.
 */
interface SignalScope

/**
 * The scopes signalled to a consumer since it was last updated. Full once any signal arrived
 * without a scope.
 */
class SignalScopes internal constructor(
    private val scopes: List<SignalScope>?
) {
    /**
     * If anything may have changed.
     */
    val isFull: Boolean
        get() = scopes == null

    /**
     * The scopes as [type], or null if full or any scope is not a [type].
     */
    fun <S : SignalScope> allOf(type: Class<S>): List<S>? {
        if (scopes == null || !scopes.all { type.isInstance(it) }) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        return scopes as List<S>
    }

    /**
     * The scopes as [S], or null if full or any scope is not an [S].
     */
    inline fun <reified S : SignalScope> allOf(): List<S>? =
        allOf(S::class.java)

    companion object {
        /**
         * Everything may have changed.
         */
        @JvmField
        val FULL = SignalScopes(null)
    }
}

/**
 * Storage a [ScopedHolderProvider] keeps for one dispatcher between asks. Dropped when the
 * dispatcher's state resets, e.g. on reload.
 */
class ProviderMemory internal constructor() {
    private var value: Any? = null

    /**
     * The stored value, or [create] it.
     */
    fun <T : Any> getOrPut(create: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return value as T? ?: create().also { value = it }
    }
}

/**
 * Storage shared by every provider asked in one pass over a dispatcher, so what one provider reads
 * (e.g. the inventory) is read once. Dropped after the pass.
 */
class ProvidePass internal constructor() {
    private val values = HashMap<Any, Any>()

    /**
     * The value stored under [key] in this pass, or [create] it.
     */
    fun <T : Any> getOrPut(key: Any, create: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return values.getOrPut(key, create) as T
    }
}

/**
 * What a [ScopedHolderProvider] is asked with.
 */
class ProvideContext internal constructor(
    val dispatcher: Dispatcher<*>,
    val scopes: SignalScopes,
    val memory: ProviderMemory,
    val pass: ProvidePass
)

/**
 * A provider that can re-check only what the signalled scopes say may have changed.
 */
interface ScopedHolderProvider : HolderProvider {
    /**
     * Provide the holders, re-checking at least what [ProvideContext.scopes] covers. With full
     * scopes, everything must be checked.
     */
    fun provide(context: ProvideContext): Collection<ProvidedHolder>
}
