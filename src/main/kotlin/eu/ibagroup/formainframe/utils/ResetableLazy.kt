package eu.ibagroup.formainframe.utils

import java.util.*
import kotlin.reflect.KProperty

/**
 * only used in ResettableLazy class which is used in one function which is not used
 * not used code
 */
class LazyManager {
  private val managedDelegates = LinkedList<Resettable>()

  internal fun register(managed: Resettable) {
    synchronized(managedDelegates) {
      managedDelegates.add(managed)
    }
  }

  fun reset() {
    synchronized(managedDelegates) {
      managedDelegates.forEach { it.reset() }
      managedDelegates.clear()
    }
  }
}

interface Resettable {
  fun reset()
}

/**
 * only used in one function which is not used
 * not used code
 */
class ResettableLazy<E>(private val manager: LazyManager, val init: () -> E) : Resettable {
  @Volatile
  var lazyHolder = makeInitBlock()

  operator fun getValue(thisRef: Any?, property: KProperty<*>): E {
    return lazyHolder.value
  }

  override fun reset() {
    lazyHolder = makeInitBlock()
  }

  private fun makeInitBlock(): Lazy<E> {
    return lazy {
      manager.register(this)
      init()
    }
  }
}

/**
 * not used code
 */
fun <E> resettableLazy(manager: LazyManager, init: () -> E): ResettableLazy<E> {
  return ResettableLazy(manager, init)
}