package com.willfp.libreforge

abstract class DelegatedList<T>(
    protected val list: List<T> = emptyList()
) : List<T> by list
