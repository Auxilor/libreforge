package com.willfp.libreforge.effects

/*
Implementation note:

Run order works with EffectList and EffectBlockList to make sure that
effects run in the order that they are meant to. Because in libreforge 4
effects are done via effect groups and blocks rather than ConfiguredEffect,
this is done twice.

First, EffectList orders all the effect groups by their average weight,
and then each effect group is itself ordered by the weight of each effect.

The weight values here are completely arbitrary. I don't even know if you need
to have them more spaced out at the ends, but it makes sense in my head, and
if it works, then I'm not going to waste time thinking about it.
 */

enum class RunOrder(
    val weight: Int
) {
    /** Run absolutely first. Only use in exceptional circumstances. */
    START(-5),

    /** Run early. */
    EARLY(-2),

    /** Run in the middle (default) */
    NORMAL(0),

    /** Run late. */
    LATE(2),

    /** Run right at the end. Only use in exceptional circumstances. */
    END(5)
}
