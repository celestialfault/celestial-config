package me.celestialfault.celestialconfig.properties

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import me.celestialfault.celestialconfig.Property
import me.celestialfault.celestialconfig.Serializer
import kotlin.reflect.KProperty

class ListProperty<T>(override val key: String, private val serializer: Serializer<T>) : MutableList<T>, Property<MutableList<T>> {
	private val list: MutableList<T> = mutableListOf()

	constructor(key: String, serializer: Serializer<T>, defaults: List<T>?) : this(key, serializer) {
		defaults?.let { this.list.addAll(it) }
	}

	override val size: Int
		get() = this.list.size

	override fun clear() = this.list.clear()
	override fun addAll(elements: Collection<T>): Boolean = this.list.addAll(elements)
	override fun addAll(index: Int, elements: Collection<T>): Boolean = this.list.addAll(elements)
	override fun add(index: Int, element: T) = this.list.add(index, element)
	override fun add(element: T): Boolean = this.list.add(element)
	override fun get(index: Int): T = this.list[index]
	override fun isEmpty(): Boolean = this.list.isEmpty()
	override fun iterator(): MutableIterator<T> = this.list.iterator()
	override fun listIterator(): MutableListIterator<T> = this.list.listIterator()
	override fun listIterator(index: Int): MutableListIterator<T> = this.list.listIterator(index)
	override fun removeAt(index: Int): T = this.list.removeAt(index)
	override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = this.list.subList(fromIndex, toIndex)
	override fun set(index: Int, element: T): T = this.list.set(index, element)
	override fun retainAll(elements: Collection<T>): Boolean = this.list.retainAll(elements)
	override fun removeAll(elements: Collection<T>): Boolean = this.list.removeAll(elements)
	override fun remove(element: T): Boolean = this.list.remove(element)
	override fun lastIndexOf(element: T): Int = this.list.lastIndexOf(element)
	override fun indexOf(element: T): Int = this.list.indexOf(element)
	override fun containsAll(elements: Collection<T>): Boolean = this.list.containsAll(elements)
	override fun contains(element: T): Boolean = this.list.contains(element)

	override fun save(): JsonElement = JsonArray().apply {
		this@ListProperty.forEach {
			add(serializer.serialize(it))
		}
	}

	override fun load(element: JsonElement) {
		if(element !is JsonArray) return
		element
			.mapNotNull { serializer.deserialize(it) }
			.forEach { add(it) }
	}

	override fun toString(): String {
		return "${save()}"
	}

	operator fun getValue(config: Any, prop: KProperty<*>): MutableList<T> = list
}
