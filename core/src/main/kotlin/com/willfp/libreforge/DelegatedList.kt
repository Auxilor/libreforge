package com.willfp.libreforge

// Cant delegate to a property, just to a constructor parameter, so here we are.
abstract class DelegatedList<T> : List<T> {
    protected val list = mutableListOf<T>()

    override val size get() = list.size
    override fun get(index: Int) = list[index]
    override fun isEmpty() = list.isEmpty()
    override fun iterator() = list.iterator()
    override fun listIterator() = list.listIterator()
    override fun listIterator(index: Int) = list.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = list.subList(fromIndex, toIndex)
    override fun lastIndexOf(element: T) = list.lastIndexOf(element)
    override fun indexOf(element: T) = list.indexOf(element)
    override fun containsAll(elements: Collection<T>) = list.containsAll(elements)
    override fun contains(element: T) = list.contains(element)
}
