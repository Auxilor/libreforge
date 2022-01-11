package com.willfp.libreforge;

import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import redempt.crunch.CompiledExpression;

import java.util.Collection;

public class CrunchHelper {
    /**
     * Evaluate {@link CompiledExpression} with {@link Collection} of values.
     * <p>
     * Kotlin for some reason didn't work using {@code *values.toTypedArray()} so
     * I've gone and coded this bit in java.
     *
     * @param expression The expression.
     * @param values     The values.
     * @return The value of the expression.
     */
    public static double evaluate(@NotNull final CompiledExpression expression,
                                  @NotNull final Collection<? extends Double> values) {
        return expression.evaluate(ArrayUtils.toPrimitive(values.toArray(new Double[0])));
    }

    private CrunchHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
