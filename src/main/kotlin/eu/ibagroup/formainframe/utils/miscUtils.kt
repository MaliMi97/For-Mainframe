package eu.ibagroup.formainframe.utils

import com.google.gson.Gson
import com.intellij.util.containers.toArray
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.concurrent.withLock
import kotlin.streams.toList

/**
 * turns Stream to MutableList
 */
fun <E> Stream<E>.toMutableList(): MutableList<E> {
  return this.toList().toMutableList()
}

/**
 * returns this as NotNull T if it is not null
 *
 * returns null if it is null
 */
inline fun <reified T> Any?.castOrNull(): T? = (this is T).runIfTrue { this as T }

/**
 * returns this if it is not null
 *
 * returns null otherwise
 */
val <E> Optional<out E>.nullable: E?
  inline get() = this.orElse(null)

/**
 * runs the block of code if this is not null
 */
inline fun <T, R> Optional<out T>.runIfPresent(block: (T) -> R): R? {
  return this.isPresent.runIfTrue {
    block(this.get())
  }
}

@JvmName("runIfPresent1")
inline fun <T, R> runIfPresent(optional: Optional<out T>, block: (T) -> R): R? {
  return optional.runIfPresent(block)
}

/**
 * returns optional of this if this is not null
 *
 * else returns an empty Optional
 */
val <E> E?.optional: Optional<E>
  inline get() = Optional.ofNullable(this)

/**
 * returns an index of the first item in the collection, which satisfies the predicate condition
 *
 * returns null if no item satisfies the predicate condition
 */
inline fun <E : Any?> List<E>.indexOf(predicate: (E) -> Boolean): Int? {
  for (i in this.indices) {
    if (predicate(this[i])) {
      return i
    }
  }
  return null
}

/**
 * NOT SURE what this does
 */
inline fun <T> lock(vararg locks: Lock?, block: () -> T): T {
  locks.forEach { it?.lock() }
  return try {
    block()
  } finally {
    locks.forEach { it?.unlock() }
  }
}

/**
 * executes block under readLock
 */
inline fun <T> ReadWriteLock.read(block: () -> T): T {
  return readLock().withLock(block)
}

/**
 * executes block under writeLock
 */
inline fun <T> ReadWriteLock.write(block: () -> T): T {
  return writeLock().withLock(block)
}

/**
 * if this is not null then it executes block under this lock
 *
 * else executes block
 */
inline fun <T> Lock?.optionalLock(block: () -> T) : T {
  return if (this != null) {
    withLock(block)
  } else {
    block()
  }
}

/**
 * NOT SURE what this does
 */
val gson by lazy { Gson() }

inline fun <reified T : Any> T.clone() = clone(T::class.java)

fun <T : Any> T.clone(clazz: Class<out T>): T {
  return with(gson) {
    fromJson(toJson(this@clone), clazz)
  }
}

/**
 * does the block of code if this is true
 *
 * returns null if this is false
 */
inline fun <T> Boolean?.runIfTrue(block: () -> T): T? {
  return if (this == true) {
    block()
  } else null
}

@JvmName("runIfTrue1")
inline fun <T> runIfTrue(aBoolean: Boolean, block: () -> T): T? {
  return aBoolean.runIfTrue(block)
}

/**
 * turns the collection to Stream if it is not empty
 *
 * otherwise returns an empty Stream
 */
fun <T> Collection<T>?.streamOrEmpty(): Stream<T> {
  return this?.stream() ?: Stream.empty()
}

/**
 * returns true if it is java class
 */
inline fun <reified T> Class<*>.isThe(): Boolean {
  return this == T::class.java
}

/**
 * NOT SURE
 */
inline fun <reified T> Stream<T>.findAnyNullable(): T? {
  return this.findAny().nullable
}

/**
 * NOT SURE
 */
fun <T> Stream<T>.filterNotNull(): Stream<T> {
  return filter(Objects::nonNull)
}

/**
 * NOT SURE
 */
fun <T, R> Stream<T>.mapNotNull(mapper: (T) -> R): Stream<R> {
  return map(mapper).filterNotNull()
}

/**
 * returns true if it is the same collection
 */
infix fun <T> Collection<T>.isTheSameAs(other: Collection<T>): Boolean {
  return this.size == other.size && (this.isEmpty() || this.containsAll(other))
}

/**
 * returns true if it is not the same collection
 */
infix fun <T> Collection<T>.isNotTheSameAs(other: Collection<T>): Boolean {
  return !(this isTheSameAs other)
}

/**
 * returns collection of elements, which are not in the other collection
 */
fun <T> Collection<T>.withoutElementsOf(other: Collection<T>): Collection<T> {
  return this.filter { thisElement ->
    other.find { otherElement -> otherElement == thisElement } == null
  }
}

/**
 * NOT SURE
 */
fun <T> Iterator<T>.stream(): Stream<T> {
  return StreamSupport.stream(Iterable { this }.spliterator(), false)
}

/**
 * NOT SURE
 */
inline fun <reified T> Collection<T>.asArray() = toArray(arrayOf())

/**
 * returns the String if it is not empty or contains only white spaces
 *
 * otherwise returns null
 */
fun String.nullIfBlank() = (isNotBlank()).runIfTrue { this }

/**
 * turns this to MutableList
 */
fun <E : Any> E.asMutableList() = mutableListOf(this)

/**
 * merge this with another MutableLists
 *
 * all duplicates are eliminated
 */
fun <R> List<R>.mergeWith(another: List<R>): MutableList<R> {
  return this.plus(another).toSet().toMutableList()
}

/**
 * unit java class
 */
val UNIT_CLASS = Unit::class.java

/**
 * does the block(this, v) if v is not null
 *
 * otherwise returns this
 */
inline fun <reified T, reified V> T.applyIfNotNull(v: V?, block: T.(V) -> T): T {
  return run {
    v?.let { block(this, it) } ?: this
  }
}