package com.willfp.libreforge.dates

/**
 * A stand-in for eco's expression evaluator (which needs a running server), covering the
 * operators holidays.yml and seasons.yml use with the same precedence as eco: `|| && | &` share the lowest
 * level and read left to right, then comparisons, then `+ -`, then `* / %`.
 *
 * Returns null for anything it can't parse, like eco.
 */
object TestExpressionEvaluator : (String) -> Double? {
    private val levels = listOf(
        listOf("||", "&&", "|", "&"),
        listOf("==", "!=", ">=", "<=", "=", ">", "<"),
        listOf("+", "-"),
        listOf("*", "/", "%")
    )

    override fun invoke(expression: String): Double? =
        try {
            Parser(expression.replace(" ", "")).parseAll()
        } catch (e: IllegalArgumentException) {
            null
        }

    private class Parser(private val source: String) {
        private var position = 0

        fun parseAll(): Double {
            val value = parseLevel(0)
            require(position == source.length) { "Unexpected '${source.substring(position)}'" }
            return value
        }

        private fun parseLevel(level: Int): Double {
            if (level == levels.size) {
                return parseUnary()
            }

            var value = parseLevel(level + 1)

            while (true) {
                val operator = levels[level].firstOrNull { source.startsWith(it, position) } ?: return value
                position += operator.length
                val right = parseLevel(level + 1)
                value = apply(operator, value, right)
            }
        }

        private fun parseUnary(): Double {
            if (source.startsWith("-", position)) {
                position++
                return -parseUnary()
            }

            if (source.startsWith("(", position)) {
                position++
                val value = parseLevel(0)
                require(source.startsWith(")", position)) { "Missing )" }
                position++
                return value
            }

            val start = position
            while (position < source.length && (source[position].isDigit() || source[position] == '.')) {
                position++
            }

            require(position > start) { "Expected a number at $start" }
            return source.substring(start, position).toDouble()
        }

        private fun apply(operator: String, left: Double, right: Double): Double {
            fun bool(condition: Boolean) = if (condition) 1.0 else 0.0

            return when (operator) {
                "||", "|" -> bool(left == 1.0 || right == 1.0)
                "&&", "&" -> bool(left == 1.0 && right == 1.0)
                "==", "=" -> bool(left == right)
                "!=" -> bool(left != right)
                ">=" -> bool(left >= right)
                "<=" -> bool(left <= right)
                ">" -> bool(left > right)
                "<" -> bool(left < right)
                "+" -> left + right
                "-" -> left - right
                "*" -> left * right
                "/" -> left / right
                "%" -> left % right
                else -> throw IllegalArgumentException(operator)
            }
        }
    }
}
