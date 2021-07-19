package eu.ibagroup.formainframe.utils

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PropertyWithLock<V>(
  private var value: V,
  private val lock: Lock
) : ReadWriteProperty<Any?, V> {

  /**
   * sets this.value to value
   */
  override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
    lock.withLock { this.value = value }
  }

  /**
   * gets value, NOT SURE: does it go only to one thread?
   */
  override fun getValue(thisRef: Any?, property: KProperty<*>) = lock.withLock {
    this.value
  }
}

class PropertyWithRWLock<V>(
  private var value: V,
  private val lock: ReadWriteLock
) : ReadWriteProperty<Any?, V> {

  /**
   * sets this.value to value
   */
  override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
    lock.write { this.value = value }
  }

  /**
   * gets value, NOT SURE: does it go only to one thread?
   */
  override fun getValue(thisRef: Any?, property: KProperty<*>) = lock.read {
    this.value
  }
}

/**
 * returns PropertyWithLock(value, ReentrantLock())
 */
fun <V> locked(value: V, lock: Lock = ReentrantLock()) = PropertyWithLock(value, lock)
/**
 * returns PropertyWithLock(value, ReentrantReadWriteLock())
 */
fun <V> rwLocked(value: V, lock: ReadWriteLock = ReentrantReadWriteLock()) = PropertyWithRWLock(value, lock)